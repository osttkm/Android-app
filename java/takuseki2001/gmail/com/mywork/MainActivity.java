package takuseki2001.gmail.com.mywork;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.*;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import java.util.Collections;
import java.util.concurrent.Executor;


//開発段階での実機　SO-03K
//用いたセンサ名　指紋センサ

public class MainActivity extends AppCompatActivity {


    private PackageManager pm = null;


    //google sheetに関するデータ
    private String ClientId = "682411977968-8cvse1i2a4t997u2rmf9e6ofldm5qati.apps.googleusercontent.com";
    private static final String SCOPE = "SheetsScopes.SPREADSHEETS";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    public GoogleAccountCredential mCredential;
    private String message;
    private int[] flag = new int[3];

    //SQLを利用しデータを保存
    private MyDatabaseHelper helper;
    private Intent intent;

    //指紋認証ようの変数。参考サイトより引用
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt biometricPrompt;
    private Handler handler = new Handler();
    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //生体認証が利用可能なのか調べる関数。使用できない場合はボタンが不活性化する
        CheckBiometricSetting();
        //指紋認証用のダイアログ処理
        shimon();

        //DB作成
        helper = new MyDatabaseHelper(getApplicationContext());

        //アカウントに関する情報。使いたいサービスはスプレッドシートへの書き込みなのでスコープに追加しておく
        mGoogleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(ClientId)
                        .requestScopes(new Scope((SheetsScopes.SPREADSHEETS)))
                        .setAccountName("takuseki2001@gmail.com")
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        //形跡がなければサインインを要求。あればアップロード機能に必要なクレデンシャルを生成
        if (GoogleSignIn.getLastSignedInAccount(this) == null) {
            signIn();
        }
        else {
            //認証情報の作成
            mCredential = GoogleAccountCredential.usingOAuth2(
                    this, Collections.singleton("https://www.googleapis.com/auth/spreadsheets"))
                    .setSelectedAccountName("takuseki2001@gmail.com")
                    .setSelectedAccount(mGoogleSignInOptions.getAccount())
                    .setBackOff(new ExponentialBackOff());
        }

    }
    //ログイン処理の関数
    //platformで許可されたアカウントのみが認証される
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            handleSignInResult();
        }
        else{
            Toast.makeText(getApplicationContext(),"認証失敗",Toast.LENGTH_SHORT).show();
        }

    }
    private void handleSignInResult() {
        //認証情報の作成
        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton("https://www.googleapis.com/auth/spreadsheets"))
                .setSelectedAccountName("takuseki2001@gmail.com")
                .setSelectedAccount(mGoogleSignInOptions.getAccount())
                .setBackOff(new ExponentialBackOff());
    }

    public void write(View v){
        //クリップボードから値を抽出する
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if(clipData != null){
            ClipData.Item item = clipData.getItemAt(0);
            message = item.getText().toString();
            Toast.makeText(this,message + "をアップロードしました",Toast.LENGTH_SHORT).show();
            if(message==null) Toast.makeText(this,"クリップボードは空です",Toast.LENGTH_SHORT).show();
        }
        SheetsAndroid sa = new SheetsAndroid();
        //クリップボードの内容のテキスト部分のみをを文字列に変換して送る
        sa.getString(message);
        //アップロード
        sa.execute(mCredential);
    }
    public void addlist(View v) {

        EditText ed_web = findViewById(R.id.web_name);
        EditText ed_pass = findViewById(R.id.ed_pass);
        EditText ed_id = findViewById(R.id.id);

        //if(ID == null){//セカンドアクティビティから戻ってないとき
        //テキストから文字列を取得
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        String name_web = ed_web.getText().toString();
        String name_pass = ed_pass.getText().toString();
        String name_id = ed_id.getText().toString();
        if(name_id.equals("")) {
            name_id = "未入力";
            flag[0] = 1;
        }
        if(name_web.equals("")){
            name_web = "未入力";
            flag[1] = 1;
        }
        if(name_pass.equals("")) {
            name_pass = "未入力";
            flag[2] = 1;
        }
        values.put("web", name_web);
        values.put("pass", name_pass);
        values.put("id", name_id);

        if (flag[0] == 1 && flag[1] == 1 && flag[2] == 1) {
            Toast.makeText(this, "入力に不備があります。登録されません", Toast.LENGTH_SHORT).show();
        }
        else {
            db.insert("myPasstb", null, values);
            Toast.makeText(this, "登録されました", Toast.LENGTH_SHORT).show();
            //リセット
            flag[0] = flag[1] = flag[2] = 0;
        }
    }


    public void go_pass(View v) {
        //指紋認証開始。認証結果によって後の処理を決定

        biometricPrompt.authenticate(promptInfo);
    }

    //生体認証ができるかできないか次第でボタンの活性不活性を決める
    //参考サイトよりコピペ
    private void CheckBiometricSetting() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                findViewById(R.id.biometricLoginButton).setVisibility(View.VISIBLE);
                findViewById(R.id.biometricLoginButton).setEnabled(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                findViewById(R.id.biometricLoginButton).setVisibility(View.INVISIBLE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                findViewById(R.id.biometricLoginButton).setVisibility(View.VISIBLE);
                findViewById(R.id.biometricLoginButton).setEnabled(false);
                break;
        }
    }

    //参考サイトよりコピペ
    //認証成功時の処理に関しては自作
    public void shimon() {
        promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("生体認証ログイン")
                        .setSubtitle("生体認証情報を利用してログインします")
                        .setNegativeButtonText("取消")
                        .build();
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "認証エラー: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                BiometricPrompt.CryptoObject authenticatedCryptoObject =
                        result.getCryptoObject();

                //認証成功時、登録されたパスワードをコピーできる画面へ移行
                intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                //キルしておく
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "認証失敗",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}

