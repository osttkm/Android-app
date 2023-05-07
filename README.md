# Android-app
学部３年前期に制作したアンドロイドアプリです．
passwordをSQLを用いて管理，またGoogle spread　sheetのapiを用いてスマートフォンのクリップボードの内容をPCのクリップボードにコピーする機能を作成しました．
多要素認証で必要とされるパスワードがスマートフォンからしか確認できず，PC側で入力する際にスマホを確認しなければならない点が不便だったためそれを簡易化するために作成しました．
セキュリティ対策としては指紋認証を導入しました．


# File
MainActivity.javaがメインのファイルです．
その他のファイルはレイアウトや，指紋認証，Google SHEETのAPI利用に関するコードです．

# DEMO
CLIPボード共有機能のでも映像です．指紋認証を含めたすべてのデモは実践動画.mp4からご覧ください．
https://user-images.githubusercontent.com/104403446/236412810-11d4025b-249c-4968-a418-ae797f772be8.mp4


# Features
指紋認証機能，Google sheets apiを利用したAndroid端末とPC間でのクリップボード共有機能を搭載しています．
指紋認証に関しては，各端末のAPIバージョン，Google apiに関してはユーザーアカウントを利用したOAuth認証が必要となります．

# Requirement
Android studioが必要です．
また，エミュレータや実機での実行はその都度APIバージョンをご確認ください．

# Usage
エニュミレータ環境 or アンドロイドの実機を用意しAndroid studioを介して実行してください．

# Note
APIなどのバージョンによりコードをそのまま利用できない場合があります．

# Author

* 尾下 拓未

** 岐阜大学　学部３年時前期作成
