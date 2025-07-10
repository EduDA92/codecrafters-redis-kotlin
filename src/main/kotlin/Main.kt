import java.net.ServerSocket

fun main(args: Array<String>) {

     var serverSocket = ServerSocket(6379)

     // Since the tester restarts your program quite often, setting SO_REUSEADDR
     // ensures that we don't run into 'Address already in use' errors
     serverSocket.reuseAddress = true
     val socket = serverSocket.accept() // Wait for connection from client.
     val inputStream = socket.inputStream

     while(true){

          val buffer = inputStream.bufferedReader().readLine()

          // if no word at all is sent exit program
          if(buffer == ""){
               break
          }

          socket.outputStream.write("+PONG\r\n".toByteArray())

     }
     println("accepted new connection")
}
