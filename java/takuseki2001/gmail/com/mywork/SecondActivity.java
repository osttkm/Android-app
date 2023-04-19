package takuseki2001.gmail.com.mywork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
//import net.sqlcipher.database.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;


import org.w3c.dom.Document;

public class SecondActivity extends AppCompatActivity{

    //コピーモードと編集モード
    private int mode = 1;
    private Intent intent;
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    //ここからデータの書き込み、読み出しなどを行う
    private Cursor item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        /*
        以下参考サイトよりほぼコピペしそれをもとにアレンジ。データの内容やクリック時の処理などは自作
        具体的には、以下からクリックしたときの処理以前までが参考範囲
         */
        //リスト表示
        ListView listView = findViewById(R.id.listview);
        //database
        helper = new MyDatabaseHelper(this);
        db = helper.getWritableDatabase();
        //データの選択
        Cursor c = db.rawQuery("select * from myPasstb",null);

        //表示するコラム
        String[] from = {"web","id"};

        //バインドするリソース。パスワード以外の２項目を表示させる
        int[] to = {android.R.id.text1,android.R.id.text2};

        //adapterの生成
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,c,from,to,0);

        //リスナ登録...リストビューが押された時の処理
        //クリックされたところのIDを取得する、直接データが返ってくることはないので注意
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ListView listview = (ListView)parent;
                        //タップされたところのデータを取得する。この後はモード別で制御
                        item = (Cursor)listview.getItemAtPosition(position);
                        if(mode == 1) {
                            //デフォルトはこのモード
                            //ここでパスワードを取得して以下でコピーすればいい
                            //クリックしたところのカーソルを取得.これにアクセスしてデータを得る
                            String pass = item.getString(item.getColumnIndex("pass"));
                            //クリップボードにシステム取得
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            //(1)ただの文字列の場合。urlなどは別途対処が必要
                            ClipData clip = ClipData.newPlainText("CopyItem", pass);
                            //クリップボードにデータを移す
                            clipboard.setPrimaryClip(clip);
                            //動作を確認
                            Toast.makeText(getApplicationContext(),"コピーしました",Toast.LENGTH_SHORT).show();
                        }
                        else if(mode == 2){
                            //編集ボタンでモードチェンジ。セッティング画面に移行
                            intent = new Intent(SecondActivity.this, Setting.class);
                            //移動先でデータを読み取るために操作したいデータのidを渡す
                            intent.putExtra("_id", item.getString(0));
                            intent.putExtra("position",position);
                            startActivity(intent);
                            //必ず使用後はカーソルを閉じる
                            item.close();
                            /*
                            ここではアプリを閉じない。パスワード機能を使うためにアプリを開く際に、内容編集を終えた後
                            くせでブラウザバックを押してしまった時もアプリが落ちないようにするため。
                            そのほかでは基本的には逐一finishするように。
                             */

                            }
                        }
                });
    }


    public void back(View v){
        //終了するときは必ずカーソルを閉じること
        if(item!=null)item.close();
        else{}
        intent = new Intent(SecondActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    //モードチェンジ　xmlファイルの方でで登録しておく
    public void to_copy(View v) {mode = 1;}
    public void to_edit(View v) {mode = 2;}
}
