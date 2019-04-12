(ns fulcro-app.api.mutations
  (:require
   [taoensso.timbre :as log]
   [fulcro-app.api.read :refer [people-db]]
   [com.wsscode.pathom.connect :as pc]
   [fulcro-app.server-components.pathom-wrappers :refer [defmutation defresolver]]
   ;;[fulcro.server :refer [defmutation]]
   ))

;; Place your server mutations here
;;(defmutation delete-person
;; "Server Mutation: Handles deleting a person on the server"
;; [{:keys [list-id person-id]}]
;; (action [{:keys [state]}]
;;         (timbre/info "Server deleting person" person-id)
;;         (swap! people-db dissoc person-id)))

(defmutation delete-person
  "Server Mutation: Handles deleting a person on the server"
  [env {:keys [list-id person-id]}]
  {::pc/sym    `delete-person
   ::pc/params [:list-id :person-id]
   ::pc/output []}
  (log/info "Server deleting person" person-id)
  (swap! people-db dissoc person-id))