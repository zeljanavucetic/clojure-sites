(ns data.mongoDB
  (:require
     [somnium.congomongo :as mongo]
     [db.work-with-csv :as csv]))



(def conn
  (mongo/make-connection "sites-clojure"
                         :host "127.0.0.17"
                         :port 27017))

;set the connection globally
(mongo/set-connection! conn)
(mongo/set-write-concern conn :strict)

(defn insert-data-into-DBcollection [collection func & args]
  "Insert data from csv files into some collection"
  (mongo/mass-insert!
    collection
    (apply func args)))



(defn initialization []
  "Initial insertion of dataset read from .csv file."
  (do
    (dorun
      (insert-data-into-DBcollection :sites csv/read-data "webSites.csv")
      )
    (insert-data-into-DBcollection :countries csv/read-data "countries.csv")
    (insert-data-into-DBcollection :languages csv/read-data "languages.csv")
    (println "Initialization done!")))


(defn -main [& args]
  (initialization))