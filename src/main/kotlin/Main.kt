import java.net.ServerSocket
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.net.Socket
import java.util.Locale.getDefault

object Storage {

    data class ValueWithExpiration(
        val value: String,
        val expiration: Long? = null,
        val setDate: Long? = null
    )

    val keyValueStorage = mutableMapOf<String, ValueWithExpiration>()
    val test = System.currentTimeMillis()
}

fun main(args: Array<String>) {

    var serverSocket = ServerSocket(6379)

    //val storage = storage

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
            "SET" -> {
                if(command.size > 3 && command[3].uppercase() == "PX"){
                    setResponse(command[1], command[2], command[4].toLong())
                } else {
                    setResponse(command[1], command[2])
                }
            }
            "GET" -> getResponse(command[1])
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

fun setResponse(key: String, value: String, expirationTime:Long = 0): ByteArray{

    if(expirationTime > 0){
        Storage.keyValueStorage[key] = Storage.ValueWithExpiration(
            value = value,
            expiration = expirationTime,
            setDate = System.currentTimeMillis()
        )
    } else {
        Storage.keyValueStorage[key] = Storage.ValueWithExpiration(value = value)
    }

    return "+OK\r\n".toByteArray()
}

fun getResponse(key: String): ByteArray{

    // First check for expiration
    if(Storage.keyValueStorage[key] != null && Storage.keyValueStorage[key]?.expiration != null){

        if(System.currentTimeMillis().minus(Storage.keyValueStorage[key]!!.setDate!!) >= Storage.keyValueStorage[key]!!.expiration!!){
            Storage.keyValueStorage.remove(key)
        }

    }

    return if(Storage.keyValueStorage[key] != null){
        "$${Storage.keyValueStorage[key]!!.value.length}\r\n${Storage.keyValueStorage[key]!!.value}\r\n".toByteArray()
    } else {
        "$-1\r\n".toByteArray()
    }

}