(ns way.terminal)

;ANSI escape codes: https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
(def esc (char 27))
(def home (char 13))
(def cls (str esc "[2J"))
(def mtl (str esc "[;H")) ;move top left
(def cursor-off (str esc "[?25l"))
(def cursor-on (str esc "[?25h"))

(def bold (str esc "[1m"))
(def green42 (str esc "[38;5;42m"))
(def green82 (str esc "[38;5;83m"))
(def reset (str esc "[0m"))

(def hh "─") 
(def HH "━") 
(def lB "▌")
(def c0M "●")

(def tip-right "🭬")
(def flag-right "🭨")
(def tip-left "🭮")
(def flag-left "🭪")
(def solid "█")
(def stripe-left-1 "🭐")
(def stripe-left-2 "🭖")


(defn color256 [i]
  (str esc "[38;5;" i "m"))

(defn get-columns []
  (try
    (let [proc (.exec (Runtime/getRuntime) "tput cols")
          _ (.waitFor proc)
          result (slurp (.getInputStream proc))]
      (Integer/parseInt (clojure.string/trim result)))
    (catch Exception _ 80))) ; fallback to 80 columns

(defn pos [x y]
  (str esc "[" x ";" y "H"))
