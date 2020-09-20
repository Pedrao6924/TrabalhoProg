package trabalhoBimestral3;


import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.logging.Logger;

public class ChatClientHandler extends Thread {

	private Socket socket;
	private String username;
	private ChatClientHandlerListener listener;
	private PrintWriter output;
	private Logger logger = Logger.getLogger(ChatClientHandler.class.getName());
        
        private static final int qntPacotesParaEnviar = 1000;
        
    
        private long tempoMenor;
        private long tempoMaior;
        
        private LocalTime horaEnviado = null;
        private LocalTime horaRecebimento = null;

        
        private long tempoTotal;
        private long tempoMedio;

        private int pacotesRecebidos;
        
	public ChatClientHandler(Socket socket, ChatClientHandlerListener listener) {
		logger.info("New user connecting...");
		this.socket = socket;
		this.listener = listener;
	}
        
        public String getUsername()
        {
            return username;
        }


	public void run() {
            logger.info("Usuario Conectado...");
		try  {
			output = new PrintWriter(socket.getOutputStream());
			Scanner input = new Scanner(socket.getInputStream());			
			while (true) {
				final String message = input.nextLine();
                                if(!message.equalsIgnoreCase("ping"))
                                {
                                    final String[] messageArray = message.split("<<<>>>");
                                    logger.info("New message: [" + message + "]");
                                    final String command = messageArray[0];
                                    final String body = messageArray[1];
                                    handleMessage(command, body);
                                }
                                else
                                    handleMessage(message);
				
								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

private void handleMessage(String msg) {
	 handleMessage(msg, ""); 
		
	}

private void handleMessage(String cmd, String body) {
	logger.info("Handling new message: command: " + cmd + ", body: " + body);
	if (cmd.equalsIgnoreCase("login")) {
		this.username = body;
		listener.handleLogin(body);
	} 
            else {
                if (cmd.equalsIgnoreCase("message")) 
		listener.handleMessage(username, body);
                else{
                    if(cmd.equalsIgnoreCase("ping"))
                         pingPong();
                }
	}
		
	}

private void pingPong() {
    tempoTotal = 0;
    tempoMaior = 0;
    tempoMenor = 0;
    tempoMedio = 0;
    pacotesRecebidos = 0;
    
    for(int i =0; i < qntPacotesParaEnviar; i++)
        ping();
	
}

public void send(String message) {
		try {
			output.println(message);
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

       
           handleMessage(cmd, body) ;
      
}
     public void pong() {     
    	 pacotesRecebidos++;
     
     horaRecebimento = LocalTime.now();
     
     long nano = ChronoUnit.NANOS.between(horaEnviado, horaRecebimento);
     
     tempoTotal = tempoTotal + nano;
     tempoMedio = tempoTotal / pacotesRecebidos;
     
     if(nano > tempoMaior)
         tempoMaior = nano;
     if(pacotesRecebidos == 1 || nano < tempoMenor)
         tempoMenor = nano;
     
                
     if(pacotesRecebidos == qntPacotesParaEnviar)
     {
         output.println("Tempo Total: "+ tempoTotal + " ns");
         output.println("Tempo Maior: "+ tempoMaior + " ns");
         output.println("Tempo Menor: "+ tempoMenor + " ns");
         output.println("Tempo Medio: "+ tempoMedio + " ns");
         output.flush();
         
         listener.finalizado(username);
     }
     }

private void ping() {

    horaEnviado = LocalTime.now();
    listener.pingPong(username);
	
}

}
