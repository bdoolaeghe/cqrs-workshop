FROM postgres:9.5

RUN apt-get update && apt-get -y install postgresql-9.5-postgis-2.3

# Debezium
# compile and install wall2json plugin
RUN apt-get install -y git make gcc postgresql-server-dev-9.5
ENV PATH=/usr/bin/pg_config:${PATH}
ENV USE_PGXS=1
# https://github.com/eulerto/wal2json/releases/tag/wal2json_1_0
ENV WAL2JSON_COMMIT_ID=d2b7fef021c46e0d429f2c1768de361069e58696
RUN git clone https://github.com/eulerto/wal2json -b master --single-branch \
    && cd /wal2json \
    && git checkout $WAL2JSON_COMMIT_ID \
    && make && make install \
    && cd / \
    && rm -rf wal2json

# protobuf
# Compile the plugin from sources and install it
ARG USE_POSTGIS=true
ENV PLUGIN_VERSION=v0.9.0.Alpha1

# Install the packages which will be required to get everything to compile
RUN apt-get update \
    && apt-get install -f -y --no-install-recommends \
        software-properties-common \
        build-essential \
        pkg-config \
        git \
        postgresql-server-dev-9.5 \
        libproj-dev \
    && if [ "$USE_POSTGIS" != "false" ]; then apt-get install -f -y --no-install-recommends \
        postgresql-9.5-postgis-2.4 \
        postgresql-9.5-postgis-2.4-scripts \
        postgis; \
       fi \
    && apt-get clean && apt-get update && apt-get install -f -y --no-install-recommends \
        liblwgeom-dev \
    && add-apt-repository "deb http://ftp.debian.org/debian testing main contrib" \
    && apt-get update && apt-get install -f -y --no-install-recommends \
        libprotobuf-c-dev=1.2.* \
    && rm -rf /var/lib/apt/lists/*

# Compile the plugin from sources and install it
RUN git clone https://github.com/debezium/postgres-decoderbufs -b $PLUGIN_VERSION --single-branch \
    && cd /postgres-decoderbufs \
    && make && make install \
    && cd / \
    && rm -rf postgres-decoderbufs

