(ns fulcro-app.client
  (:require [fulcro.client :as fc]
            [fulcro-app.ui.root :as root]
            [fulcro.client.network :as net]
            [fulcro.client.data-fetch :as df]
            [fulcro-app.api.mutations :as api]))

(defonce SPA (atom nil))

(defn mount []
  (reset! SPA (fc/mount @SPA root/Root "app")))

(defn start []
  (mount))

(def secured-request-middleware
  ;; The CSRF token is embedded via server_components/html.clj
  (->
    (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))
    (net/wrap-fulcro-request)))

(defn ^:export init []
  (reset! SPA (fc/new-fulcro-client
                :started-callback (fn [fulcro-app]
                                    (df/load fulcro-app :all-users root/User)
                                    (df/load fulcro-app :current-user root/Person)
                                    (df/load fulcro-app :my-enemies root/Person 
                                             {:target [:person-list/by-id 
                                                       :enemies 
                                                       :person-list/people]
                                              :post-mutation `api/sort-friends})
                                    (df/load fulcro-app :my-friends root/Person 
                                             {:target [:person-list/by-id 
                                                       :friends 
                                                       :person-list/people]})
                                    )
                ;; This ensures your client can talk to a CSRF-protected server.
                ;; See middleware.clj to see how the token is embedded into the HTML
                :networking {:remote (net/fulcro-http-remote
                                       {:url                "/api"
                                        :request-middleware secured-request-middleware})}))
  (start))