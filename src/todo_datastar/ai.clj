(ns todo-datastar.ai
  (:require [wkok.openai-clojure.api :as ai]))

(def open-router-key "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")

(def tools
  [{:type "function"
    :name "get_weather"
    :description "Get current temperature for a given location."
    :parameters {:type "object"
                 :properties {:location {:type "string"
                                         :description "City and country e.g. Bogotá, Colombia"}}
                 :required ["location"]
                 :additionalProperties false}}
   {:type "function"
    :name "get_iconic_food"
    :description "Get iconic food for a given location."
    :parameters {:type "object"
                 :properties {:location {:type "string"
                                         :description "City and country e.g. Bogotá, Colombia"}}
                 :required ["location"]
                 :additionalProperties false}}])


(defn try-create [] (ai/create-chat-completion
                     {:model "google/gemini-2.5-flash"
                      :messages [{:role "system" :content "You are a helpful assistant."}
                                 {:role "user" :content "What is the iconic food in Bogotá, Colombia? Tell me the function tool name was used"}
                                 ;; {:role "assistant" :content "The Los Angeles Dodgers won the World Series in 2020."}
                                 ;; {:role "user" :content "Where was it played?"}
                                 ]
                      :functions tools
                      ;;:function_call "auto"
           											}
                     {:api-key open-router-key,
                      :api-endpoint "https://openrouter.ai/api/v1"}))

(try-create)