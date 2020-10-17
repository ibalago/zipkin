/*
 * Copyright 2015-2020 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.storage.cassandra.v1;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.querybuilder.Insert;
import java.util.concurrent.ThreadLocalRandom;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static zipkin2.storage.cassandra.v1.CassandraUtil.toByteBuffer;
import static zipkin2.storage.cassandra.v1.IndexTraceId.BUCKET_COUNT;
import static zipkin2.storage.cassandra.v1.Tables.ANNOTATIONS_INDEX;

// QueryRequest.annotations/binaryAnnotations
final class IndexTraceIdByAnnotation extends IndexTraceId.Factory {
  IndexTraceIdByAnnotation(CassandraStorage storage, int indexTtl) {
    super(storage, ANNOTATIONS_INDEX, indexTtl);
  }

  @Override public Insert declarePartitionKey(Insert insert) {
    return insert
      .value("annotation", bindMarker())
      .value("bucket", bindMarker());
  }

  @Override public BoundStatement bindPartitionKey(BoundStatement bound, String partitionKey) {
    return bound
      .setBytes(2, toByteBuffer(partitionKey))
      .setInt(3, ThreadLocalRandom.current().nextInt(BUCKET_COUNT));
  }
}
