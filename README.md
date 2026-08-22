# CreateGame
ゲーム作成用ver001

###<<<Main.javaの動かし方>>><br>
1.「./mvnw clean package」をターミナルで実行<br>
2.「java -jar target/create-game-1.0-SNAPSHOT.jar」で実行<br>
<br>
###<br>
①サーバー側＞＞＞```ServerSocket serverSocket = new ServerSocket(5000);Socket socket = serverSocket.accept();```<br>
②クライアント側＞＞＞```Socket socket = new Socket("サーバーのIPアドレス", 5000);```


