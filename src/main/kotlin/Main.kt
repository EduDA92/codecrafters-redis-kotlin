import java.net.ServerSocket
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

fun main(args: Array<String>) = runBlocking {

     var serverSocket = ServerSocket(6379)

     serverSocket.reuseAddress = true

     while(true){

          val socket = serverSocket.accept() // Wait for connection from client.

          launch{

               while (true){

                    val input = socket.inputStream.bufferedReader().readLine()
                    if (input == ""){
                         break
                    }
                    socket.outputStream.write("+PONG\r\n".toByteArray())
                    println("Pong sent")

               }
          }
     }


}