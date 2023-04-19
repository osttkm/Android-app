package takuseki2001.gmail.com.mywork;


import android.os.AsyncTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;


//ファイルのアップロードは非同期処理。AsynTaskを拡張する
public class SheetsAndroid extends AsyncTask<GoogleAccountCredential,Void,List<String>>{
    //コンストラクタを作成して、メインアクティビティの方からボタン操作をする
    SheetsAndroid(){
        super();
    }

    //必要な変数類。主に通信や通信データについての情報
    private static final String APPLICATION_NAME = "takuseki2001@gmail.com";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
    private String SPREAD_SHEETS_ID = "1tN8AU0dk3_1SkWcBtCbAA5Fls3GmJNxOBaD8OTFhfL8";
    //データを変換するための変数
    private List<List<Object>> values = new ArrayList<List<Object>>();
    private  List<String> results = new ArrayList<String>();
    public ValueRange response=null;
    //Google Sheets
    private Sheets service = null;
    //アップロードするメッセージ
    private String message = null;

    //アップロードしたい文字列を取得。代入
    public void getString(String m){
        message = m;
    }

    //以下、アップロードの流れは参考サイトより参照
    //必要な変数の獲得についてはアレンジ
    @Override
    protected List<String> doInBackground(GoogleAccountCredential... credentials) {
        //認証のためにcredentialが必要。メインアクティビティから調達。配列扱いになるので配列の０番目を得る
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials[0])
                .setApplicationName(APPLICATION_NAME)
                .build();
        //今回は(1,1)に配置するためこれだけだが、複数データを送りたい場合はここをループさせる
        values = Arrays.asList(
                Arrays.asList(message)
        );
        //送信するための型に変換
        response = new ValueRange().setValues(values);
        //以下は非同期処理
        try {
            //データの読み取りは以下の通り。スプレッドシートIDと読み取る範囲を指定する
            /*response = service.spreadsheets().values()
                    .get("1tN8AU0dk3_1SkWcBtCbAA5Fls3GmJNxOBaD8OTFhfL8", "data!A1:C1")
                    .execute();*/
            this.service.spreadsheets().values()
                    .update(SPREAD_SHEETS_ID,"data!A1:A1",response)
                    .setValueInputOption("RAW") //RAWはオプション
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //関数のリターンは別に何でもいい。AsynTaskの第三変数次第
        return results;
    }

}


