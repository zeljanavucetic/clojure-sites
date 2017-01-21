 (ns start
   (:require [routes.handler :as handler]))

(defn -main [& args]
  (handler/start-server))