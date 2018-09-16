package io.github.armay.moneytransfer.api.rest;

public interface Resource {

    class ExceptionWrapper {

        private String error;

        public ExceptionWrapper(Exception e) {
            this.error = e.getMessage();
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

    }

}
