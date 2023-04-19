import Control.Concurrent
import Text.Printf

main = do
    pepiseCola <- newMVar 2000
    guaranaQuate <- newMVar 2000
    guaranaPoloNorte <- newMVar 2000
    -- clienteID <- newMVar 1
    controle <- newMVar True

    forkIO $ cliente pepiseCola "Pepise-Cola" controle
    forkIO $ refil pepiseCola "Pepise-Cola" controle
    
    forkIO $ cliente guaranaQuate "Guaraná Quate" controle
    forkIO $ refil guaranaQuate "Guaraná Quate" controle

    forkIO $ cliente guaranaPoloNorte "Guaraná Polo Norte" controle
    forkIO $ refil guaranaPoloNorte "Guaraná Polo Norte" controle

refil :: MVar Int -> String -> MVar Bool -> IO ()
refil refri nomeRefri control = loop
    where
        loop = do
            ctrl <- takeMVar control
            if ctrl == True then do
                refriQtde <- takeMVar refri
                if refriQtde <= 1000 then do
                    threadDelay 1500000
                    printf "O refrigerante %s foi reabastecido com 1000 ml, e agora possui %d ml\n" nomeRefri (refriQtde+1000)
                    putMVar refri (refriQtde + 1000)
                else do
                    putMVar refri refriQtde
                putMVar control True

            else do
                putMVar control True
                
            loop

cliente :: MVar Int -> String -> MVar Bool -> IO ()
cliente refri nomeRefri control = do
    clienteID <- newMVar (1::Int)
    loop clienteID
    where
        loop clienteID = do
            ctrl <- takeMVar control
            if ctrl == True then do
                clienteAtual <- takeMVar clienteID
                threadDelay 1000000
                printf "O cliente %d do refrigerante %s está enchendo seu copo\n" clienteAtual nomeRefri
                putMVar clienteID (clienteAtual + 1)
                qtde <- takeMVar refri
                putMVar refri (qtde - 300)
                putMVar control True

            else do
                putMVar control True
                
            loop clienteID


