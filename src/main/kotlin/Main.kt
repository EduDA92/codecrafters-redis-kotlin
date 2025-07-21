import java.net.ServerSocket
import kotlinx.coroutines.*
import java.net.Socket

fun main(args: Array<String>) {

    var serverSocket = ServerSocket(6379)

    serverSocket.reuseAddress = true



    while (true) {

        val socket = serverSocket.accept() // Wait for connection from client.

        CoroutineScope(Dispatchers.IO).launch {
            handleConnection(socket)
        }

    }

}

fun handleConnection(socket: Socket) {

    while (socket.isConnected) {
        socket.inputStream.bufferedReader().readLine()
        socket.outputStream.write("+PONG\r\n".toByteArray())
    }

    socket.close()

}
