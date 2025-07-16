import java.net.ServerSocket
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main(args: Array<String>) {
     var serverSocket = ServerSocket(6379)

     // Since the tester restarts your program quite often, setting SO_REUSEADDR
     // ensures that we don't run into 'Address already in use' errors
     serverSocket.reuseAddress = true

     coroutineScope {

          while(true){

               println("Waiting for new connection")
               val socket = serverSocket.accept() // Wait for connection from client.
               println("accepted new connection")



               launch{
                    while(true){
                         val buffer = socket.inputStream.bufferedReader().readLine()
                         socket.outputStream.write("+PONG\r\n".toByteArray())
                         println("Pong sent")
                    }
               }


          }
     }
}
