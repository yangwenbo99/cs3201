from socket import *
print ('***    SIMPLE TEXT MESSENGER    ***\n'
       '***    Ver. 1.0.0.1a    ***\n'
       '(C)onnect peer\n'
       '(W)ait for the other peer connecting\n'
       '(Q)uit')
op=raw_input("Please choose :")

if op=="w" or op=="W":
    
    serverIP = raw_input("\nInput local machine IP: ")
    serverPort = int(raw_input("\nInput the port that you want to bind: "))
    serverSocket = socket(AF_INET,SOCK_STREAM)
    print '\nWait for connecting...'
    serverSocket.bind((serverIP,serverPort))
    print 'Bind IP and port succesfully!'
    
    op = 'N'
    while op=='N' or op =='n':
        serverSocket.listen(1)
        print 'Start listening...'
        connectionSocket, addr = serverSocket.accept()
        print 'Accept connecting: ',addr[0]

        while op=='N' or op =='n':
            sentence = connectionSocket.recv(1024)
            if sentence=="Quit":
                print 'Your peer has left the conversation. If you want to quit the application, press any key.'
                op = raw_input("If you do not want to quit the application, press N or n.")
                if op!='N' and op !='n':
                    connectionSocket.close()
                break
            else:
                print addr[0],' says: ',sentence
                mysentence = raw_input("Please input the message:")
                print '\nYou say: ',mysentence
                connectionSocket.send(mysentence)

elif op=="c" or op=="C":
    serverIP = raw_input("\nRemote IP: ")
    serverPort = int(raw_input("\nand port: "))
    clientSocket = socket(AF_INET, SOCK_STREAM)
    clientSocket.connect((serverIP,serverPort))
    while 1:
        sentence = raw_input('\nInput the message:')
        if sentence=="Quit":
            clientSocket.send(sentence)
            op = raw_input('\nYou will quit the application, press any key.')
            clientSocket.close()
            break
        print '\nYou say: ', sentence
        clientSocket.send(sentence)
        modifiedSentence = clientSocket.recv(1024)
        print serverIP,' says: ', modifiedSentence
print ('Quit the application Whatsup...\n'
       'Thanks for using.\n')
