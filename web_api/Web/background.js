var copy = function(){
        //セミコロンは改行してあればいらない
        const request = new XMLHttpRequest();
        request.open('GET',
        "https://script.google.com/macros/s/AKfycbwdDo0u41ZRZUurZ8piBlrO389xsNjGjLIvCW25GvrRIxcJSDMXlR2r-0KSfEE2SeZJ/exec");
        request.responseType = "json";
        request.onload = function () {
            let data = this.response;
            document.write(data['data'])
            document.write("1234")

            var textVal = data
             // テキストエリアを用意する
            var copyFrom = document.createElement("textarea")
            // テキストエリアへ値をセット
            copyFrom.textContent = textVal
            // bodyタグの要素を取得
            var bodyElm = document.getElementsByTagName("body")[0]
            // 子要素にテキストエリアを配置
            bodyElm.appendChild(copyFrom)
            // テキストエリアの値を選択
            copyFrom.select()
            // コピーコマンド発行
            var retVal = document.execCommand('copy')
            // 追加テキストエリアを削除
            bodyElm.removeChild(copyFrom)
            // 処理結果を返却
            return retVal
        };
        request.send();
    }

    function touch(){
        chrome.browserAction.onClicked.addListener(copy)
    }
    var myfun = touch()