(ns way.conf (:require [clojure.java.io :as io]
            [babashka.fs :as fs]
            [clojure.edn :as edn]))

(defn find-wayf 
  "Search recursively upward for .wayf file"
  []
  (let [cwd (fs/file (System/getProperty "user.dir"))]
    (loop [wd cwd]
      (cond 
        (.exists (fs/file wd ".wayf")) (fs/file wd ".wayf")
        (nil? (fs/parent wd))          nil 
        :else (recur (fs/parent wd))
        ))))

(def template-wayf 
  {:search-keys []
   :direntry-note "Note displayed upon entry to this directory"
   :usage-reminders {:example ["Structured usage reminders"]}
   :commands {:example "echo 'this is an example command'"}
   })


(defn create-wayf []
  (spit ".wayf" template-wayf)
  (fs/file ".wayf"))


(defn find-conf 
  "Find central config file based on OS"
  []
  (let [osname   (System/getProperty "os.name")
        conffile (cond 
                   (re-matches #"(?i).*win.*" osname) "C:\\ProgramData\\wayf\\wayf.edn"
                   (re-matches #"(?i).*mac" osname) "/System/Library/wayf/wayf.edn"
                   :else "/etc/wayf/wayf.edn")
        ]
    conffile))

(defn load-conf []
  (try
    (let [file (find-conf)
          raw  (slurp file)
          data (edn/read-string raw)]
      data)
    (catch java.io.FileNotFoundException e nil)))
