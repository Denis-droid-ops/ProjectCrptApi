package com.kuznetsov.projectCrptApi;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CrptApi {

    private final long milis;
    private final int requestLimit;
    private final HttpClient httpClient;
    private final Semaphore semaphore;
    private boolean timerOff;
    private final Lock lock;
    private final Timer timer;
    private final AtomicInteger counter;
    private final AtomicInteger nullCounterNum;


    public CrptApi(long milis, int requestLimit) {
        this.milis = milis;
        this.requestLimit = requestLimit;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.semaphore = new Semaphore(requestLimit,true);
        counter = new AtomicInteger(0);
        this.timer = new Timer();
        this.lock = new ReentrantLock();
        this.timerOff = true;
        this.nullCounterNum = new AtomicInteger(0);

    }

    class RequestData{
        private final String requestBody;
        private final Map<String,String> requestParams;

        public RequestData(String requestBody, Map<String, String> requestParams) {
            this.requestBody = requestBody;
            this.requestParams = requestParams;
        }

        public String getRequestBody() {
            return requestBody;
        }

        public Map<String, String> getRequestParams() {
            return requestParams;
        }
    }


    class Document{

            private final Description description;

            @JsonProperty("doc_id")
            private final String docId;

            @JsonProperty("doc_status")
            private final String docStatus;

            @JsonProperty("doc_type")
            private final DocType docType;

            private final boolean importRequest;

            @JsonProperty("owner_inn")
            private final String ownerInn;

            @JsonProperty("participant_inn")
            private final String participantInn;

            @JsonProperty("producer_inn")
            private final String producerInn;

            @JsonProperty("production_date")
            private final LocalDate productionDate;

            @JsonProperty("production_type")
            private final String productionType;

            private final List<Product> products;

            @JsonProperty("reg_date")
            private final LocalDate regDate;

            @JsonProperty("reg_number")
            private final String regNumber;

            public Document(Description description,
                            String docId,
                            String docStatus,
                            DocType docType,
                            boolean importRequest,
                            String ownerInn,
                            String participantInn,
                            String producerInn,
                            LocalDate productionDate,
                            String productionType,
                            List<Product> products,
                            LocalDate regDate,
                            String regNumber) {
                this.description = description;
                this.docId = docId;
                this.docStatus = docStatus;
                this.docType = docType;
                this.importRequest = importRequest;
                this.ownerInn = ownerInn;
                this.participantInn = participantInn;
                this.producerInn = producerInn;
                this.productionDate = productionDate;
                this.productionType = productionType;
                this.products = products;
                this.regDate = regDate;
                this.regNumber = regNumber;
            }

            public Description getDescription() {
                return description;
            }

            public String getDocId() {
                return docId;
            }

            public String getDocStatus() {
                return docStatus;
            }

            public DocType getDocType() {
                return docType;
            }

            public boolean isImportRequest() {
                return importRequest;
            }

            public String getOwnerInn() {
                return ownerInn;
            }

            public String getParticipantInn() {
                return participantInn;
            }

            public String getProducerInn() {
                return producerInn;
            }

            public LocalDate getProductionDate() {
                return productionDate;
            }

            public String getProductionType() {
                return productionType;
            }

            public List<Product> getProducts() {
                return products;
            }

            public LocalDate getRegDate() {
                return regDate;
            }

            public String getRegNumber() {
                return regNumber;
            }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Document document = (Document) o;
            return Objects.equals(docId, document.docId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(docId);
        }

        enum DocType{
                LP_INTRODUCE_GOODS
            }

            static final class Description{
               private final String participantInn;

                Description(String participantInn) {
                    this.participantInn = participantInn;
                }

                public String getParticipantInn() {
                    return participantInn;
                }
            }

            static final class Product{

                @JsonProperty("certificate_document")
                private final String certificateDocument;

                @JsonProperty("certificate_document_date")
                private final LocalDate certificateDocumentDate;

                @JsonProperty("certificate_document_number")
                private final String certificateDocumentNumber;

                @JsonProperty("owner_inn")
                private final String ownerInn;

                @JsonProperty("producer_inn")
                private final String producerInn;

                @JsonProperty("production_date")
                private final LocalDate productionDate;

                @JsonProperty("tnved_code")
                private final String tnvedCode;

                @JsonProperty("uit_code")
                private final String uitCode;

                @JsonProperty("uitu_code")
                private final String uituCode;

                public Product(String certificateDocument,
                               LocalDate certificateDocumentDate,
                               String certificateDocumentNumber,
                               String ownerInn,
                               String producerInn,
                               LocalDate productionDate,
                               String tnvedCode,
                               String uitCode,
                               String uituCode) {
                    this.certificateDocument = certificateDocument;
                    this.certificateDocumentDate = certificateDocumentDate;
                    this.certificateDocumentNumber = certificateDocumentNumber;
                    this.ownerInn = ownerInn;
                    this.producerInn = producerInn;
                    this.productionDate = productionDate;
                    this.tnvedCode = tnvedCode;
                    this.uitCode = uitCode;
                    this.uituCode = uituCode;
                }

                public String getCertificateDocument() {
                    return certificateDocument;
                }

                public LocalDate getCertificateDocumentDate() {
                    return certificateDocumentDate;
                }

                public String getCertificateDocumentNumber() {
                    return certificateDocumentNumber;
                }

                public String getOwnerInn() {
                    return ownerInn;
                }

                public String getProducerInn() {
                    return producerInn;
                }

                public LocalDate getProductionDate() {
                    return productionDate;
                }

                public String getTnvedCode() {
                    return tnvedCode;
                }

                public String getUitCode() {
                    return uitCode;
                }

                public String getUituCode() {
                    return uituCode;
                }
            }
        }


        //метод для проверки
    public static void main(String[] args) {
        String CREATE_FOR_RUS_PROD_ITEM_URI =
                "https://ismp.crpt.ru/api/v3/lk/documents/create";
        CrptApi.Document.Description description = new CrptApi.Document.Description("444");
        CrptApi.Document.Product product = new CrptApi.Document.Product("string",
                LocalDate.of(2020, 1,23),
                "string",
                "string",
                "string",
                LocalDate.of(2020,1,23),
                "string",
                "string",
                "string");

        CrptApi crptApi = new CrptApi(3000,2);

        CrptApi.Document document1 = crptApi.new Document(description,
                "1",
                "string",
                CrptApi.Document.DocType.LP_INTRODUCE_GOODS,
                true,
                "string",
                "string",
                "string",
                LocalDate.of(2020,1,23),
                "string",
                List.of(product),
                LocalDate.of(2020,1,23),
                "string");

        CrptApi.Document document2 = crptApi.new Document(description,
                "2",
                "string",
                CrptApi.Document.DocType.LP_INTRODUCE_GOODS,
                true,
                "string",
                "string",
                "string",
                LocalDate.of(2020,1,23),
                "string",
                List.of(product),
                LocalDate.of(2020,1,23),
                "string");

        CrptApi.Document document3 = crptApi.new Document(description,
                "3",
                "string",
                CrptApi.Document.DocType.LP_INTRODUCE_GOODS,
                true,
                "string",
                "string",
                "string",
                LocalDate.of(2020,1,23),
                "string",
                List.of(product),
                LocalDate.of(2020,1,23),
                "string");


        for (int i = 0;i<50;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+" поток старт");
                    try {
                        crptApi.createDoc(document1,"a",CREATE_FOR_RUS_PROD_ITEM_URI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"thread"+i);
            thread.start();
        }

    }

    public CompletableFuture<HttpResponse<String>> createDoc(Document document, String signature,String uri) throws InterruptedException {
        semaphore.acquire();
        lock.lock();
        if(timerOff){
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    semaphore.release(requestLimit);
                    System.out.println("Освобождение разрешений");
                    if(counter.get()==0){
                        nullCounterNum.incrementAndGet();
                    }else {
                        if(nullCounterNum.get()>0){
                            nullCounterNum.decrementAndGet();
                        }
                    }
                    if(nullCounterNum.get()==10){
                        System.out.println("Длительное время не происходит запросов, " +
                                "введите 'exit' для завершения программы," +
                                " или любую другую клавишу для продолжения");
                        Scanner scanner = new Scanner(System.in);
                        if(scanner.next().equals("exit")){
                            System.exit(0);
                        }
                        nullCounterNum.set(0);
                    }
                    counter.set(0);

                }
            },milis,milis);
        }
        timerOff = false;
        lock.unlock();
        System.out.println("Пропущенные семафором потоки:   "+Thread.currentThread().getName());

        CompletableFuture<HttpResponse<String>> response = null;
        try {
            response = doPostRequest(uri,new RequestData(documentToJson(document),
                                                         Map.of("signature",signature)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            counter.incrementAndGet();
            System.out.println("Завершение запроса  "+Thread.currentThread().getName());
        }
        return response;
    }

    private synchronized String documentToJson(Document document) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(document);
    }

    private CompletableFuture<HttpResponse<String>> doPostRequest(String uri, RequestData requestData) throws IOException, InterruptedException {
        System.out.println("Выполнение запроса  "+ Thread.currentThread().getName());
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(uri))
                .header("Content-Type", "application/json")
                .header("signature",requestData.getRequestParams().get("signature"))
                .POST(HttpRequest.BodyPublishers.ofString(requestData.getRequestBody()))
                .build();
        CompletableFuture<HttpResponse<String>> httpResponse =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return httpResponse;
    }

}
