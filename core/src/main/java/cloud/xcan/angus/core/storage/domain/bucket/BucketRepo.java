package cloud.xcan.angus.core.storage.domain.bucket;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;


public interface BucketRepo extends BaseRepository<Bucket, Long> {

  Optional<Bucket> findByName(String name);

}
