(ns way.server
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import [java.net ServerSocket Socket])
  )

(defn serve [socket]
  (with-open [client (.accept socket)]
    (let [out (io/writer (.getOutputStream client))
          in  (io/reader (.getInputStream client))
         ]
      (println "Connection accepted from: " (.getRemoteSocketAddress client))
      (println "Message: "
               (edn/read (java.io.PushbackReader. in)))
      (.write out "You have found your way")
      (.flush out)
    )))

(defn systemd-serve [args]
  (let [listen-fd-str (System/getenv "LISTEN_FDS")
        listen-fd (if listen-fd-str 
                    (Integer/parseInt listen-fd-str)
                    (do (println "LISTEN_FDS environment variable not set")
                        (System/exit 1)))]
    (try
      (let [server-socket (java.net.ServerSocket. nil nil listen-fd)]
        (println "Systemd activated server listening on: " listen-fd)
        (serve server-socket))
      (catch Exception e
        (println "Error creating systemd server socket:" (.getMessage e))
        (System/exit 1)))))

(defn cli-serve [args]
  (with-open [ss (new ServerSocket 3250 )]
    (println "CLI activated server listening on: 3250")
    (serve ss)))

(defn sync [args]
  (println "Sync functionality not yet implemented"))
