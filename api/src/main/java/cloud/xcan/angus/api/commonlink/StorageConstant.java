package cloud.xcan.angus.api.commonlink;

import cloud.xcan.angus.spec.annotations.Unmodifiable;

public interface StorageConstant {

  String DEFAULT_DATA_FILE_BIZ_KEY = "angusTesterDataFiles";
  String DEFAULT_APP_CODE = "AngusTester";

  String DOWNLOAD_FILE_API_PATH = "/api/v1/file";
  String DOWNLOAD_FILE_PUB_API_PATH = "/pubapi/v1/file";
  String FILE_QUERY_ID_NAME = "fid";
  String FILE_PUBLIC_TOKEN_NAME = "fpt";

  String SHARE_QUERY_ID_NAME = "sid";
  String SHARE_PUBLIC_TOKEN_NAME = "spt";

  @Unmodifiable
  int MAX_FILE_DIR_LEVEL = 10;

  @Unmodifiable
  int MAX_COMPRESS_FILE_NUM = 5000;
  @Unmodifiable
  int MAX_COMPRESS_FILE_SIZE = 20 * 1024 * 1024 * 1024;

  /**
   * Maximum number of supported files per request
   */
  @Unmodifiable
  int MAX_REQUEST_FILES_NUM = 200;

  int SPACE_PERMISSION_NUM = 8;

  int MAX_BUCKET_NAME_LENGTH = 40;
  int MIN_BUCKET_NAME_LENGTH = 3;

}
