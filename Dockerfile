FROM eclipse-temurin:21
RUN mkdir /opt/eider
COPY target/appassembler /opt/eider
ENV PATH="$PATH:/opt/eider/bin"
