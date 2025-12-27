(ns way.main
  (:require [bbb.core :refer [run-cmd]]
            [way.notecheck :as notecheck]
            [way.runner :as runner]
            [way.shell :as shell]
            [way.edit :as edit]
            [way.server :as server]
            [way.wprint :as wprint])
  (:gen-class))

(defn -main [& args]
  (run-cmd args
    {:app      {:command     "way"
                :description "The System Wayfinder - helps admins navigate command-line environments"
                :version     "1.0.0"}
     :global-opts []
     :commands
     [{:command     "notecheck"
       :description "Check for reminders in current directory"
       :runs        (fn [opts] (notecheck/check (:_arguments opts)))}
      
      {:command     "show"
       :description "Show notes and commands for current directory"
       :runs        (fn [opts] (notecheck/show (:_arguments opts)))}
      
      {:command     "login"
       :description "Login functionality"
       :runs        (fn [opts] (notecheck/login (:_arguments opts)))}
      
      {:command     "run"
       :description "Run a stored command"
       :runs        (fn [opts] (runner/run (:_arguments opts)))}
      
      {:command     "shell"
       :description "Generate shell integration code"
       :runs        (fn [opts] (shell/shell (:_arguments opts)))}
      
      {:command     "edit"
       :description "Edit notes or commands"
       :runs        (fn [opts] (edit/edit (:_arguments opts)))}
      
      {:command     "sync"
       :description "Sync with server"
       :runs        (fn [opts] (server/sync (:_arguments opts)))}
      
      {:command     "serve"
       :description "Start server"
       :runs        (fn [opts] (server/cli-serve (:_arguments opts)))}
      
      {:command     "systemd-serve"
       :description "Start systemd server"
       :runs        (fn [opts] (server/systemd-serve (:_arguments opts)))}]}))

