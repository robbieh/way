(ns way.edit
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.pprint :as pprint]
            [way.conf :as conf]))

(defn launch-editor [filename]
  (let [editor          (or (System/getenv "VISUAL") "vim")
        process-builder (java.lang.ProcessBuilder. (list editor filename))
        inherit         (java.lang.ProcessBuilder$Redirect/INHERIT)
       ]
    (.redirectOutput process-builder inherit)
    (.redirectInput process-builder inherit)
    (.redirectError process-builder inherit)
    (.waitFor (.start process-builder))
  ))

(def comments 
  {:direntry-note 
   "#Enter text to be displayed upon entry to this directory"
   :usage-reminders
   "#Enter your usage reminder"
   :commands
   "#Enter sh command string"
   :reminders
   "#Enter your reminders below"
   })

(defn do-edit [wayf path-vec]
  (let [tmpfile (java.io.File/createTempFile "wayfinder-direntry" "txt")
        tmpname (str tmpfile)
        oldconf (-> wayf slurp edn/read-string)
        oldtext (get-in oldconf path-vec "")
        comment-text (get comments (first path-vec))
        ]
    (println oldtext)
    ;prep file to be edited
    (condp instance? oldtext
      String  
        (spit tmpfile (str comment-text "\n" oldtext))
      clojure.lang.PersistentVector 
        (spit tmpfile (apply str comment-text "\n" (interpose "\n" oldtext)))
      (spit tmpfile (str comment-text "\n" oldtext))
      )

    ;edit
    (launch-editor tmpname)

    ;read in changes
    (let [newtext (->> tmpfile io/reader line-seq 
                       (remove #(re-matches #"#.*" %)) 
                       )
          newtext (condp instance? oldtext
                    String 
                       (->> newtext (interpose "\n" )
                           (apply str))
                    clojure.lang.PersistentVector 
                       (vec newtext))
          config  (edn/read-string (slurp wayf))
          updated-config (assoc-in config path-vec newtext)
          ]
      (with-open [w (io/writer wayf)]
        (pprint/pprint updated-config w))
      )
    )
  )

;(let [estr (slurp (conf/find-wayf))
;      nodes (redn/parse-string estr)
;      ]
;  (str (redn/assoc-in nodes [:commands :foo ]"do foo"))
;  )


;map more convenient cli names to key names used in .wayf file
(def edit-type-map {:direntry :direntry-note
                     :usage :usage-reminders
                     :command :commands
                     })
(def edit-keys (set (vals edit-type-map)))
(def edit-depth-map {:direntry-note 1
                     :usage-reminders 2
                     :commands 2
                     })
(defn edit [args]
  (let [arg-keys (mapv keyword args)
        edit-type (get edit-type-map (first arg-keys))
        arg-keys (assoc arg-keys 0 edit-type)
        wayf    (or (conf/find-wayf) (conf/create-wayf))
        ]
    (when-not (contains? edit-keys edit-type)
      (print "available edit subkey types: ")
      (println (apply str (interpose " " (map name (keys edit-type-map)))))
      (System/exit 1)
      )
    (when (< (count args) 
             (get edit-depth-map (first arg-keys)))
      (println (str (first args) " requires a subkey") )
      (System/exit 1)
      )
    (do-edit wayf arg-keys)
    ))


;(let [estr (slurp (conf/find-wayf))
;      nodes (redn/parse-string estr)
;      ]
;  (str (redn/assoc-in nodes [:commands :foo ]"do foo"))
;  )
;  (println (str (redn/assoc-in nodes [:deps 'my-other-dep] {:mvn/version "0.1.2"})))
