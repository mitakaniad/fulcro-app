(ns fulcro-app.api.read
  (:require
   ;;[fulcro.server :refer [defquery-root defquery-entity defmutation]]
   [com.wsscode.pathom.connect :as pc]
   [fulcro-app.server-components.pathom-wrappers :refer [defmutation defresolver]]
   [taoensso.timbre :as log]
   ))

(def people-db (atom {1  {:db/id 1 :person/name "Bert" :person/age 55 :person/relation :friend}
                      2  {:db/id 2 :person/name "Sally" :person/age 22 :person/relation :friend}
                      3  {:db/id 3 :person/name "Allie" :person/age 76 :person/relation :enemy}
                      4  {:db/id 4 :person/name "Zoe" :person/age 32 :person/relation :friend}
                      99 {:db/id 99 :person/name "Me" :person/role "admin"}}))

;;(defquery-root :current-user
;; "Queries for the current user and returns it to the client"
;; (value [env params]
;;        (get @people-db 99)))

(defn get-people [kind keys]
 (->> @people-db
      vals
      (filter #(= kind (:person/relation %)))
      vec))

;;(defquery-root :my-friends
;; "Queries for friends and returns them to the client"
;; (value [{:keys [query]} params]
;;        (get-people :friend query)))

;;(defquery-root :my-enemies
;; "Queries for enemies and returns them to the client"
;; (value [{:keys [query]} params]
;;        (get-people :enemy query)))

;;(defquery-entity :person/by-id
;; "Server query for allowing the client to pull an individual person from the database"
;; (value [env id params]
;;   ; the update is just so we can see it change in the UI
;;        (update (get @people-db id) :person/name str " (refreshed)")))

(defresolver current-user-resolver
  "Resolve queries for :current-user."
  [env input]
  {;;GIVEN nothing 
   ::pc/output [{:current-user [:db/id :person/name]}]}
  {:current-user (get @people-db 99)})

(defresolver my-friends-resolver
  [{:keys [query] :as env} input]
  {;;GIVEN nothing 
   ::pc/output [{:my-friends [:db/id :person/name :person/age]}]}
  {:my-friends (get-people :friend query)})

(defresolver my-enemies-resolver
  [{:keys [query] :as env} input]
  {;;GIVEN nothing 
   ::pc/output [{:my-enemies [:db/id :person/name :person/age]}]}
  {:my-enemies (get-people :enemy query)})

(defresolver person-by-id-resolver
  "Server query for allowing the client to pull an individual person from the database"
  [{:keys [query]} {:keys [person/by-id]}]
  {::pc/input  #{:person/by-id}
   ::pc/output [:db/id :person/name :person/age]}
  (update (get @people-db by-id) :person/name str " (refreshed)"))