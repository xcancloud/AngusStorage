package cloud.xcan.angus.core.storage.domain;


public interface StorageMessage {

  String STORAGE_SETTING_MODIFY_TYPE_WARN_CODE = "BST001";
  String STORAGE_SETTING_MODIFY_TYPE_WARN = "xcm.storage.setting.modify.type.warn";

  String BUCKET_NAME_FORMAT_ERROR = "xcm.storage.bucket.format.error";
  String BUCKET_DELETED_NOT_EMPTY_CODE = "BST101";
  String BUCKET_DELETED_NOT_EMPTY = "xcm.storage.bucket.deleted.not.empty";
  String BUCKET_BIZ_DELETED_NOT_EMPTY_CODE = "BST102";
  String BUCKET_BIZ_DELETED_NOT_EMPTY = "xcm.storage.bucket.biz.deleted.not.empty";
  String BUCKET_NON_CUS_DELETED_ERROR_CODE = "BST103";
  String BUCKET_NON_CUS_DELETED_ERROR = "xcm.storage.bucket.non.customized.deleted.error.t";
  String BUCKET_NON_CUS_BIZ_DELETED_ERROR_CODE = "BST104";
  String BUCKET_NON_CUS_BIZ_DELETED_ERROR = "xcm.storage.bucket.non.customized.biz.deleted.error.t";
  String BUCKET_CREATE_FAIL_CODE = "BST105";
  String BUCKET_CREATE_FAIL_T = "xcm.storage.bucket.create.fail.t";

  String SPACE_NAME_EXISTED_T = "xcm.storage.space.name.existed.t";
  String SPACE_DELETED_NOT_EMPTY_CODE = "BST201";
  String SPACE_DELETED_NOT_EMPTY = "xcm.storage.space.deleted.not.empty.t";

  String OBJECT_DIRECTORY_NAME_EXISTED_T = "xcm.storage.object.directory.name.existed.t";
  String OBJECT_LOCAL_DOWNLOAD_ERROR_CODE = "BST301";
  String OBJECT_LOCAL_DOWNLOAD_ERROR_T = "xcm.storage.object.local.download.error.t";

  /*<******************Space#Auth(BST501 ~ BST520)******************>*/
  String SPACE_FORBID_AUTH_CREATOR_CODE = "BST501";
  String SPACE_FORBID_AUTH_CREATOR = "xcm.storage.space.forbid.auth.creator";
  String SPACE_NO_AUTH_CODE = "BST502";
  String SPACE_NO_AUTH = "xcm.storage.space.no.auth.t";
  String SPACE_NO_TARGET_AUTH_CODE = "BST503";
  String SPACE_NO_TARGET_AUTH = "xcm.storage.space.no.target.auth.t";

  /*<******************Space#Quota(BST521 ~ BST530)******************>*/
  String SPACE_SIZE_OVER_LIMIT_CODE = "BST521";
  String SPACE_SIZE_OVER_LIMIT_T = "xcm.storage.space.size.over.limit.t";

  /*<******************Space#Share(BST531 ~ BST40)******************>*/
  String SHARE_OBJECT_OVER_LIMIT_CODE = "BST531";
  String SHARE_OBJECT_OVER_LIMIT_T = "xcm.storage.space.share.object.over.limit.t";

}
