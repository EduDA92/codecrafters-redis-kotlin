import java.net.ServerSocket
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.net.Socket
import java.util.Locale
import java.util.Locale.getDefault

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

        var command = parseCommand(socket.inputStream.bufferedReader())

        val response = when(command[0].uppercase(getDefault())){
            "PING" -> "+PONG\r\n".toByteArray()
            "ECHO" -> "$${command[1].length}\r\n${command[1]}\r\n".toByteArray()
            else -> {"-ERR unknown command '${command[0]}'\r\n".toByteArray()}
        }
        socket.outputStream.write(response)
        socket.outputStream.flush()
    }

}


fun parseCommand(reader: BufferedReader): List<String>{

    return when(reader.read().toChar()){
        '*' -> parseArray(reader)
        else -> emptyList()
    }

}

fun parseArray(reader: BufferedReader): List<String>{

    var parsedArray = mutableListOf<String>()

    for(i in 1..reader.readLine().toInt()){
        reader.readLine() //element length
        parsedArray.add(reader.readLine()) //element
    }

    return parsedArray
}

