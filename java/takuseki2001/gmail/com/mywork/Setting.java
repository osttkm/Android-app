package takuseki2001.gmail.com.mywork;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {

    private Intent intent;
    //リストビュー画面から受け取るデータのid
    private String key;
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    private EditText ed_pass,ed_id,ed_web;
    private Cursor cursor;
    private String return_ID;
    //カーソルの位置
    private int position;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_setting);
        helper = new MyDatabaseHelper(getApplicationContext());
        db = helper.getReadableDatabase();
        intent = getIntent();

        //データベースの更新に必要。更新する場所を指定する
        key = intent.getStringExtra("_id");
        //データを読み込むところ。カーソルを用いるので前の画面のタップした場所をもらう
        position = intent.getIntExtra("position",0);


        //keyはIDとしてつかう。SQLで使えるよう_idに代入しておく
        return_ID = String.format("_id=%s",key);
        //データを用意。カーソルの中身はリストビューの上から順になっている。常に０から始まる。
        //データベースのIDとは関係ないので注意が必要
        cursor = db.query(
                "myPasstb",
                new String[]{"web","id","pass"},
                null,
                null,
                null,null,null
        );

        //登録しておく
        ed_id = findViewById(R.id._id);
        ed_web = findViewById(R.id.web);
        ed_pass = findViewById(R.id.pass);
        //タップしたリストビューの位置までカーソルを移動させ、その中身を出力
        readData();
    }
    public void back(View v){
        //戻るときの処理。データは更新する。
        Updata();
        intent = new Intent(Setting.this,SecondActivity.class);
        startActivity(intent);
        //カーソルは必ず閉じる
        cursor.close();
        //キルしておく
        finish();
    }

    public void delete(View v){
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete("myPasstb",return_ID,null);
        intent = new Intent(Setting.this,SecondActivity.class);
        startActivity(intent);
        cursor.close();
        finish();
    }

    public void Updata(){
        SQLiteDatabase db = helper.getReadableDatabase();

        String web = ed_web.getText().toString();
        String id = ed_id.getText().toString();
        String pass = ed_pass.getText().toString();

        ContentValues upvalue = new ContentValues();
        upvalue.put("web",web);
        upvalue.put("id",id);
        upvalue.put("pass",pass);
        //IDの場所を更新
        db.update("myPasstb",upvalue,return_ID,null);
    }
    public void readData(){
        cursor.moveToFirst();
        System.out.println(cursor.getPosition());
        //IDとリストビュー(カーソル)の位置は違う。IDとカーソルの位置関係はない
        //リストビューは０番から
        //IDは１から
        cursor.moveToPosition(position);//
        if(cursor != null){
            ed_web.setText(cursor.getString(0));
            ed_id.setText(cursor.getString(1));
            ed_pass.setText(cursor.getString(2));
        }
        else{
            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
        }

    }
}
