package cloud.xcan.angus.core.storage.application.cmd.file.impl;

import static cloud.xcan.angus.api.commonlink.StorageConstant.FILE_QUERY_ID_NAME;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertUnauthorized;
import static cloud.xcan.angus.core.storage.application.converter.FileConverter.toUploadObjectFile;
import static cloud.xcan.angus.core.storage.application.converter.SpaceObjectConverter.toUpdateSpaceObject;
import static cloud.xcan.angus.core.storage.infra.utils.FileNameSecurityUtil.sanitizeFileName;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isApi;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.remote.CommonMessage.SHARE_PASSWORD_ERROR_T;
import static cloud.xcan.angus.remote.CommonMessage.SHARE_TOKEN_ERROR_T;
import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT_11;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PUBLIC_TOKEN_LENGTH;
import static cloud.xcan.angus.spec.http.ContentType.TYPE_OCTET_STREAM;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getToken;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.StringUtils.getUrlParameterValues;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

import cloud.xcan.angus.api.commonlink.CompressFormat;
import cloud.xcan.angus.api.enums.FileType;
import cloud.xcan.angus.api.storage.file.FileRemote;
import cloud.xcan.angus.api.storage.file.dto.FileDownloadDto;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.storage.application.cmd.file.ObjectFileCmd;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceCmd;
import cloud.xcan.angus.core.storage.application.cmd.space.SpaceObjectCmd;
import cloud.xcan.angus.core.storage.application.query.bucket.BucketBizConfigQuery;
import cloud.xcan.angus.core.storage.application.query.file.ObjectFileQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceAuthQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceObjectQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceQuery;
import cloud.xcan.angus.core.storage.application.query.space.SpaceShareQuery;
import cloud.xcan.angus.core.storage.domain.bucket.config.BucketBizConfig;
import cloud.xcan.angus.core.storage.domain.file.ObjectFile;
import cloud.xcan.angus.core.storage.domain.file.ObjectFileRepo;
import cloud.xcan.angus.core.storage.domain.space.Space;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObject;
import cloud.xcan.angus.core.storage.domain.space.object.SpaceObjectRepo;
import cloud.xcan.angus.core.storage.domain.space.share.SpaceShare;
import cloud.xcan.angus.core.storage.infra.store.ObjectClient;
import cloud.xcan.angus.core.storage.infra.store.impl.ObjectClientFactory;
import cloud.xcan.angus.core.storage.infra.store.model.AccessControl;
import cloud.xcan.angus.core.storage.infra.store.model.ProcessCommand;
import cloud.xcan.angus.core.storage.infra.store.utils.ImageUtils;
import cloud.xcan.angus.core.storage.infra.store.utils.MockMultipartFile;
import cloud.xcan.angus.core.utils.SpringAppDirUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.experimental.StandardCharsets;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.DateUtils;
import cloud.xcan.angus.spec.utils.ZipUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Biz
@Slf4j
public class ObjectFileCmdImpl extends CommCmd<ObjectFile, Long> implements ObjectFileCmd {

  @Resource
  private ObjectFileRepo objectFileRepo;

  @Resource
  private ObjectFileQuery objectFileQuery;

  @Resource
  private SpaceObjectRepo spaceObjectRepo;

  @Resource
  private SpaceObjectCmd spaceObjectCmd;

  @Resource
  private SpaceQuery spaceQuery;

  @Resource
  private SpaceCmd spaceCmd;

  @Resource
  private SpaceObjectQuery spaceObjectQuery;

  @Resource
  private SpaceShareQuery spaceShareQuery;

  @Resource
  private SpaceAuthQuery spaceAuthQuery;

  @Resource
  private FileRemote fileRemote;

  @Resource
  private BucketBizConfigQuery bucketBizConfigQuery;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private SpringAppDirUtils appDirUtils;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<ObjectFile> upload(String bizKey, Long spaceId, Long parentDirId, Long projectId,
      boolean ignoreLocalStore, Long outFid, boolean extraFiles, MultipartFile... files) {
    return new BizTemplate<List<ObjectFile>>(false) {
      BucketBizConfig bizConfigDb;
      Space spaceDb;
      SpaceObject parentDirectoryDb;
      final ObjectClient objectClient = ObjectClientFactory.current();

      @Override
      protected void checkParams() {
        /// --------- Check and BucketBizConfig and Space -------------///
        // Check that at least one parameter of spaceId and bizKey is required and consistent
        assertTrue(isNotEmpty(bizKey) || isNotEmpty(spaceId),
            "At least one parameter of spaceId and bizKey is required");

        if (nonNull(spaceId)) {
          // Check the space existed
          spaceDb = spaceQuery.checkAndFind(spaceId);
          if (isNotEmpty(bizKey)) {
            assertTrue(isEmpty(bizKey) || bizKey.equals(spaceDb.getBizKey()),
                format("Space[%s] and bizKey[%s] is inconsistent", spaceId, bizKey));
            bizConfigDb = bucketBizConfigQuery.findByBizKey(bizKey);
          } else {
            bizConfigDb = bucketBizConfigQuery.findByBizKey(spaceDb.getBizKey());
          }
        } else {
          // Check the allocation business configuration
          if (!extraFiles) {
            bizConfigDb = bucketBizConfigQuery.findByBizKey(bizKey);
            // Check and init non-tenant business(customized=false) space
            spaceDb = spaceCmd.findAndInitByBizKey(bizConfigDb, bizKey);
          }
        }

        if (nonNull(spaceDb)) {
          assertTrue(!bizConfigDb.isMultiTenantCtrl()
                  || getOptTenantId().equals(spaceDb.getTenantId()),
              format("Upload space[%s] tenant[%s] error", spaceDb.getId(), getOptTenantId()));

          // Check the parent directory existed
          if (nonNull(parentDirId)) {
            parentDirectoryDb = spaceObjectQuery.checkAndDirectory(parentDirId);
            assertTrue(isNull(spaceId) || spaceId.equals(parentDirectoryDb.getSpaceId()),
                format("Space[%s] and parent directory[%s] is inconsistent", spaceDb.getId(),
                    parentDirectoryDb.getSpaceId()));
          }

          // Check the space write object permission where tenant business
          // Non-tenant business allows tenant's all users to upload
          if (bizConfigDb.getEnabledAuth()) {
            spaceAuthQuery.checkObjectWriteAuth(getUserId(), spaceDb.getId());
          }

          // Check the space size quota
          spaceQuery.checkSpaceSizeQuota(spaceDb);
          // Check the tenant size quota.
          // Note: The space quota configuration may be unlimited, but it is limited based on the actual total size of uploaded files.
          spaceQuery.checkTenantSizeQuota(spaceDb);
        }
      }

      @Override
      protected List<ObjectFile> process() {
        if (extraFiles) {
          List<ObjectFile> allUploadedFiles = new ArrayList<>();
          for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String lowerFileName = fileName.toLowerCase();
            if (!lowerFileName.endsWith(".zip") && !lowerFileName.endsWith(".tar")
                && !lowerFileName.endsWith(".tar.gz") && !lowerFileName.endsWith(".tgz")) {
              throw ProtocolException.of(
                  "Unsupported file format. Only ZIP (.zip) or TAR (.tar, .tar.gz, .tgz) formats are supported.");
            }

            boolean isNewSpace = false;
            if (isNull(spaceDb)) {
              bizConfigDb = bucketBizConfigQuery.findByBizKey(bizKey);
              spaceDb = spaceCmd.addCustomized(bizConfigDb, fileName.split("\\.")[0], projectId);
              isNewSpace = true;
            }

            // 解压压缩包结构（包含目录、文件类型，层级关系）
            // 如果是新空间，将解压后一级目录下内容添加到空间；如果是旧空间，先创建一级目录，再将解压后一级目录下内容添加到空间对应目录下
            // 遍历包结构，如果是目录创建目录，如果是文件上传文件并保存文件对象信息到本地
            // 如果是目录，创建完目录后，继续遍历目录下的文件和子目录
            // 对于每一级都需要记录统计信息、包括父级关联信息
            try {
              List<ObjectFile> extractedFiles = extractAndUploadArchive(file, isNewSpace,
                  bizConfigDb, spaceDb, parentDirectoryDb, objectClient);
              allUploadedFiles.addAll(extractedFiles);
            } catch (Exception e) {
              log.error("Extract and upload archive file {} error", fileName, e);
              throw SysException.of(format("Extract and upload archive file %s error: %s",
                  fileName, e.getMessage()));
            }
          }
          return allUploadedFiles;
        }

        List<SpaceObject> spaceObjects = new ArrayList<>(files.length);
        List<ObjectFile> objectFiles = new ArrayList<>(files.length);
        // Save file to disk or S3 service
        for (MultipartFile file : files) {
          Long fid = uidGenerator.getUID();
          Long safeOutFid = isNull(outFid) ? fid : outFid;
          Long oid = uidGenerator.getUID();
          String fileName = isNotEmpty(file.getOriginalFilename())
              ? sanitizeFileName(file.getOriginalFilename())
              : isNotEmpty(file.getName()) ? file.getName() : "undefined";
          String uniqueName = getBaseName(fileName) + "." + safeOutFid + "."
              + getExtension(fileName);
          String objectName = objectClient.getObjectName(bizConfigDb.getBucketName(),
              bizConfigDb.getBizKey(), getTenantId(), spaceDb.getId(), uniqueName);
          String publicToken = bizConfigDb.getPublicTokenAuth()
              ? RandomStringUtils.randomAlphanumeric(MAX_PUBLIC_TOKEN_LENGTH) : null;
          String downloadUrl = objectFileQuery.assembleDownloadUrl(fid, fileName,
              bizConfigDb, publicToken);
          String storeAddress = objectClient
              .getObjectUrl(bizConfigDb.getBucketName(), objectName);
          Long parentDirId = nonNull(parentDirectoryDb)
              ? parentDirectoryDb.getId() : DEFAULT_ROOT_PID;
          int level = nonNull(parentDirectoryDb) ? parentDirectoryDb.getLevel() + 1 : 1;
          String parentLikeId = nonNull(parentDirectoryDb)
              ? (isNotEmpty(parentDirectoryDb.getParentLikeId())
              ? parentDirectoryDb.getParentLikeId() + "-" + parentDirectoryDb.getId()
              : String.valueOf(parentDirectoryDb.getId())) : "";
          SpaceObject spaceObject = toUpdateSpaceObject(file, fileName, fid, oid, spaceDb,
              parentDirId, level, parentLikeId, objectClient.getStoreType());
          ObjectFile objectFile = toUploadObjectFile(file, fileName, objectName,
              uniqueName, downloadUrl, fid, oid, storeAddress, spaceDb, publicToken,
              objectClient.getStoreType(), bizConfigDb, parentDirId, applicationInfo);
          try {
            if (!ignoreLocalStore || objectClient.getProperties().isS3Platform()) {
              objectClient.putObject(bizConfigDb.getBucketName(), objectName,
                  file.getInputStream(), AccessControl.Private);
            }
            spaceObjects.add(spaceObject);
            objectFiles.add(objectFile);
          } catch (Exception e) {
            deleteSuccessFileWhenFail(objectFiles, file, e);
          }
        }

        // Save file info
        batchInsert0(objectFiles);

        // Save space object
        spaceObjectCmd.fileAdd0(spaceDb.getId(), parentDirectoryDb, spaceObjects);

        return objectFiles;
      }

      private void deleteSuccessFileWhenFail(List<ObjectFile> objectFiles, MultipartFile file,
          Exception e) {
        if (isNotEmpty(objectFiles)) {
          for (ObjectFile successFile : objectFiles) {
            try {
              objectClient.removeObject(successFile.getBucketName(), successFile.getPath());
            } catch (Exception ex) {
              log.error("Rollback upload success file {} failed: {}",
                  successFile.getStoreAddress(), ex.getMessage());
            }
          }
        }
        log.error(format("Write file %s to disk error", file.getName()), e);
        throw SysException.of(format("Write file %s to disk error: %s",
            file.getName(), e.getMessage()));
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public ObjectFile download(@NotNull String filename, Long fid, String fpt, String fproc,
      Long sid, String spt, String password) {
    return new BizTemplate<ObjectFile>(false) {
      ObjectFile objectFileDb;
      ProcessCommand processCommand;
      BucketBizConfig bucketBizConfigDb;
      final boolean isShareDownload = Objects.nonNull(sid) && isNotEmpty(spt);

      @Override
      protected void checkParams() {
        // Check the fproc valid and Judge whether the processed file existed when fproc is not empty
        if (isNotEmpty(fproc)) {
          processCommand = ProcessCommand.parse(fproc);
          ProtocolAssert.assertTrue(nonNull(processCommand), "fproc format error");
        }

        // Check the file object existed, judge priority fid > filename(uniqueName)
        // 1) Check file object existed where fid not null
        if (isNotEmpty(fid)) {
          objectFileDb = objectFileQuery.checkAndFind(fid);
        }
        // 2) Check the file object existed where fid is null, filename must is uniqueName
        if (isEmpty(objectFileDb)) {
          objectFileDb = objectFileQuery.findByUniqueName(filename);
        }

        // Check the permission
        bucketBizConfigDb = bucketBizConfigQuery.findByBizKey(objectFileDb.getBizKey());
        if (isShareDownload) {
          // Check the share existed
          SpaceShare spaceShareDb = spaceShareQuery.checkAndFind(sid);
          // Check the spt(public token) authorization
          assertUnauthorized(spt.equals(spaceShareDb.getPublicToken()), SHARE_TOKEN_ERROR_T);
          // Check the password where public0 = false
          assertUnauthorized(spaceShareDb.getPublic0() ||
              spaceShareDb.getPassword().equals(password), SHARE_PASSWORD_ERROR_T);
          // Check the share oid existed
          assertResourceNotFound(spaceShareDb.getWideObjectIds()
              .contains(objectFileDb.getOid()), objectFileDb.getOid());
        } else {
          if (!bucketBizConfigDb.getPublicAccess()) {
            // Check the tenant data permission
            assertResourceNotFound(!bucketBizConfigDb.getMultiTenantCtrl() ||
                objectFileDb.getTenantId().equals(getTenantId()), filename);
            // Check the space object permission
            if (bucketBizConfigDb.getEnabledAuth()) {
              spaceAuthQuery.checkObjectReadAuth(getUserId(), objectFileDb.getSpaceId());
            }
          }
        }

        // Check fpt(public token) authorization
        assertUnauthorized(!bucketBizConfigDb.getPublicTokenAuth() ||
                (Objects.nonNull(fpt) && fpt.equals(objectFileDb.getPublicToken())),
            "Public token is empty or unauthorized");
      }

      @Override
      protected ObjectFile process() {
        // Fallback to the default content type if type could not be determined
        MediaType mediaType = MediaType.parseMediaType(isEmpty(objectFileDb.getContentType())
            ? TYPE_OCTET_STREAM : objectFileDb.getContentType());
        // Set client cache
        objectFileDb.setCacheAge(bucketBizConfigDb.getCacheAge());
        // Set content type
        objectFileDb.setMediaType(mediaType);

        // Forward download when local storage instances are inconsistent
        if (notSameInstance()) {
          objectFileDb.setForwardUrl(getForwardUrl());
          return objectFileDb;
        }

        // When it does not need to be processed or found
        if (notProcessFile(mediaType)) {
          return objectFileDb;
        }

        // Return directly when processed file existed
        String ext = getExtension(objectFileDb.getName());
        long newFid = objectFileDb.getId();
        String uniqueFileName = getUniqueFilename(ext, newFid);
        ObjectFile processedObject = objectFileRepo.findValidByUniqueName(uniqueFileName)
            .orElse(null);
        if (Objects.nonNull(processedObject)) {
          processedObject.setCacheAge(bucketBizConfigDb.getCacheAge());
          processedObject.setMediaType(mediaType);
          return processedObject;
        }

        // Generate process files to local store
        String srcFilePath = null, targetFilePath = null, newFilename;
        try {
          srcFilePath = objectFileDb.isLocalPlatform() ?
              ObjectClientFactory.of(objectFileDb.getStoreType())
                  .getObjectPath(objectFileDb.getPath()) : downloadS3ToTemp(objectFileDb);
          targetFilePath = FilenameUtils.getFullPath(srcFilePath) + uniqueFileName;
          ImageUtils.zoomByCommand(srcFilePath, targetFilePath, processCommand);
        } catch (Exception e) {
          log.error("Zoom image exception", e);
          deleteS3TempFile(srcFilePath, e);
          return objectFileDb;
        }
        try {
          newFilename = getBaseName(objectFileDb.getName())
              + (processCommand.isZoomScale() ? ".s_" + processCommand.getScale()
              : ".w_" + processCommand.getWidth() + "_h_" + processCommand.getHeight()) + "." + ext;
          MultipartFile file = new MockMultipartFile(newFilename, newFilename,
              objectFileDb.getContentType(), new FileInputStream(new File(targetFilePath)));
          if (!isUserAction()) {
            PrincipalContext.get().setTenantId(objectFileDb.getTenantId());
          }
          processedObject = upload(objectFileDb.getBizKey(), objectFileDb.getSpaceId(),
              objectFileDb.getParentDirectoryId(), null,true, newFid, false, file).get(0);
          processedObject.setCacheAge(bucketBizConfigDb.getCacheAge());
          processedObject.setMediaType(mediaType);
          return processedObject;
        } catch (IOException e) {
          log.error("Create zoom image {} InputStream or upload biz exception {}", srcFilePath,
              e.getMessage());
          deleteLocalTargetWhenUploadFail(srcFilePath, targetFilePath, e);
          return objectFileDb;
        } finally {
          // Clear s3 temp file
          deleteS3SrcAndTargetWhenUploadFinish(srcFilePath, targetFilePath);
        }
      }

      /**
       * File service may be multi instance
       */
      private boolean notSameInstance() {
        return objectFileDb.isLocalPlatform() && !applicationInfo.getInstanceId()
            .equals(objectFileDb.getInstanceId());
      }

      private String getForwardUrl() {
        String forwardUrl = format("http://%s/%s/v1/file/%s?fid=%s",
            objectFileDb.getInstanceId(), isApi() ? "api" : "pubapi",
            URLEncoder.encode(filename, StandardCharsets.UTF_8), fid);
        forwardUrl += isNotEmpty(fpt) ? "&fpt=" + fpt : "";
        forwardUrl += isNotEmpty(fproc) ? "&fproc=" + fproc : "";
        forwardUrl += isApi() ? "&access_token=" + getToken() : "";
        return forwardUrl;
      }

      private void deleteS3SrcAndTargetWhenUploadFinish(String srcFilePath, String targetFilePath) {
        if (objectFileDb.isS3Platform()) {
          if (isNotEmpty(srcFilePath)) {
            try {
              FileUtils.forceDelete(new File(srcFilePath));
              log.info("Delete s3 src temp zoom image {} ", srcFilePath);
            } catch (IOException ioException) {
              log.error("Delete s3 src temp zoom image {}", srcFilePath);
            }
          }
          if (isNotEmpty(targetFilePath)) {
            try {
              FileUtils.forceDelete(new File(targetFilePath));
              log.info("Delete s3 target temp zoom image {} ", targetFilePath);
            } catch (IOException ioException) {
              log.error("Delete s3 target temp zoom image {}", targetFilePath);
            }
          }
        }
      }

      private void deleteLocalTargetWhenUploadFail(String srcFilePath, String targetFilePath,
          IOException e) {
        if (objectFileDb.isLocalPlatform() && isNotEmpty(targetFilePath)) {
          try {
            FileUtils.forceDelete(new File(targetFilePath));
            log.info("Delete local target temp zoom image {} ", srcFilePath);
          } catch (IOException ioException) {
            log.error("Delete local target temp zoom image {} exception: {}", srcFilePath,
                e.getMessage());
          }
        }
      }

      private void deleteS3TempFile(String srcFilePath, Exception e) {
        if (objectFileDb.isS3Platform() && isNotEmpty(srcFilePath)) {
          try {
            FileUtils.forceDelete(new File(srcFilePath));
          } catch (IOException ioException) {
            log.error("Delete temp zoom image {} exception: {}", srcFilePath, e.getMessage());
          }
        }
      }

      private String getUniqueFilename(String ext, long newFid) {
        return FilenameUtils.getBaseName(objectFileDb.getName()) + (
            processCommand.isZoomScale() ? ".s_" + processCommand.getScale()
                : ".w_" + processCommand.getWidth() + "_h_" + processCommand.getHeight()) + "."
            + newFid + "." + ext;
      }

      private boolean notProcessFile(MediaType mediaType) {
        return Objects.isNull(processCommand) || !"image".equals(mediaType.getType())
            || Objects.isNull(fid);
      }
    }.execute();
  }

  /**
   * Archive multiple files into one compressed file.
   * <p>
   * Directory and multi directory compression are not supported.
   */
  @Override
  public ObjectFile compress(String name, Long parentDirectoryId, CompressFormat format,
      Set<String> urls, Set<Long> ids) {
    return new BizTemplate<ObjectFile>() {
      final Set<Long> compressObjectIds = new HashSet<>();
      SpaceObject parentDirectoryDb;
      Long spaceId;
      List<SpaceObject> spaceObjectsDb;

      @Override
      protected void checkParams() {
        // Check that at least one parameter of spaceId and bizKey is required and consistent
        assertTrue(isNotEmpty(urls) || isNotEmpty(ids),
            "At least one parameter of urls and ids is required");

        // Check and get compress file ids
        checkAndGetCompressObjectIds();

        // Check and get parent directory
        boolean hashParentDirectory = Objects.nonNull(parentDirectoryId)
            && !parentDirectoryId.equals(DEFAULT_ROOT_PID);
        if (hashParentDirectory) {
          parentDirectoryDb = spaceObjectQuery.checkAndDirectory(parentDirectoryId);
        }

        // Check and get files
        checkAndGetFileObjects();

        // Compressed files and directories must be the same space
        List<Long> spaceIds = spaceObjectRepo.findSpaceIdByIdIn(compressObjectIds);
        assertTrue(isNotEmpty(spaceIds) && spaceIds.size() == 1,
            "Only files with the same space can be compressed");

        spaceId = spaceIds.get(0);
        assertTrue(Objects.isNull(parentDirectoryDb) || parentDirectoryDb.getSpaceId()
            .equals(spaceId), "The file and destination directory must be in the same space");

        // Check space write object permission
        spaceAuthQuery.checkObjectWriteAuth(getUserId(), spaceId);

        // Check compress quota
        // spaceQuery.checkSpaceQuota(parentDirectoryDb);
      }

      @Override
      protected ObjectFile process() {
        ObjectFile compressFile = new ObjectFile();
        try {
          // Save all files to tmp directory
          File[] source = new File[spaceObjectsDb.size()];
          for (int i = 0; i < spaceObjectsDb.size(); i++) {
            SpaceObject object = spaceObjectsDb.get(i);
            File file = new File(COMPRESS_FILE_TEM_PATH + object.getName());
            judgeAndCreateTemp(file);
            InputStream downloadInputStream = fileRemote
                .download("dummy", new FileDownloadDto().setFid(object.getFid()))
                .getBody().getInputStream();
            FileUtils.copyInputStreamToFile(downloadInputStream, file);
            source[i] = file;
          }

          // Compress files
          File desFile = new File(COMPRESS_FILE_DES_TEM_PATH + (isEmpty(name)
              ? DateUtils.formatDate(new Date(), DATE_FMT_11) : name));
          judgeAndCreateDesTemp(desFile);
          ZipUtils zipUtils = new ZipUtils(source, desFile);
          zipUtils.compress();

          // Save or upload compress file TODO

          // Save file info
          insert0(compressFile);

          // Save space object
          SpaceObject spaceObject = new SpaceObject();
          spaceObjectCmd.fileAdd0(spaceId, parentDirectoryDb, singletonList(spaceObject));
        } catch (Exception e) {
          log.error("Compress file error:", e);
          throw SysException.of("Compressed file exception: " + e.getMessage());
        }
        return compressFile;
      }

      private void checkAndGetFileObjects() {
        spaceObjectsDb = spaceObjectRepo.findByIdInAndType(compressObjectIds, FileType.FILE);
        assertResourceNotFound(spaceObjectsDb, format("Not found file %s",
            compressObjectIds.iterator().next()));
        if (spaceObjectsDb.size() != compressObjectIds.size()) {
          spaceObjectsDb.stream().map(SpaceObject::getId).toList()
              .forEach(compressObjectIds::remove);
          throw ResourceNotFound.of(format("Not found file %s",
              compressObjectIds.iterator().next()));
        }
      }

      private void checkAndGetCompressObjectIds() {
        if (isNotEmpty(ids)) {
          compressObjectIds.addAll(ids);
        }
        if (isNotEmpty(urls)) {
          for (String url : urls) {
            Set<String> fileIds = getUrlParameterValues(url, FILE_QUERY_ID_NAME);
            assertNotEmpty(fileIds,
                format("Missing %s parameter in url %s", FILE_QUERY_ID_NAME, url));
            assertTrue(fileIds.size() < 2, format("Fid value %s is duplicate", fileIds));
            compressObjectIds.add(Long.parseLong(fileIds.iterator().next()));
          }
        }
      }
    }.execute();
  }

  private String downloadS3ToTemp(ObjectFile objectFileDb) throws IOException {
    String targetFilePath = appDirUtils.getTmpDir() + "fproc" + File.separator
        + UUID.randomUUID().toString() + File.separator + objectFileDb.getName();
    FileUtils.copyInputStreamToFile(ObjectClientFactory.of(objectFileDb.getStoreType())
            .getObject(objectFileDb.getBucketName(), objectFileDb.getPath()).getObjectContent(),
        new File(targetFilePath));
    return targetFilePath;
  }

  private void judgeAndCreateTemp(File file) {
    if (!file.getParentFile().exists()) {
      if (!file.getParentFile().mkdirs()) {
        throw SysException.of("Exception in creating compressed file temporary directory");
      }
    }
  }

  private void judgeAndCreateDesTemp(File desFile) {
    if (!desFile.getParentFile().exists()) {
      if (!desFile.getParentFile().mkdirs()) {
        throw SysException.of(
            "Exception in creating compressed file destination temporary directory");
      }
    }
  }

  /**
   * 解压压缩包并上传文件
   *
   * @param archiveFile       压缩包文件
   * @param isNewSpace        是否是新空间
   * @param bizConfigDb       业务配置
   * @param spaceDb           空间对象
   * @param parentDirectoryDb 父目录对象
   * @param objectClient      对象存储客户端
   * @return 上传的文件列表
   */
  private List<ObjectFile> extractAndUploadArchive(MultipartFile archiveFile, boolean isNewSpace,
      BucketBizConfig bizConfigDb, Space spaceDb, SpaceObject parentDirectoryDb,
      ObjectClient objectClient) throws IOException {
    String tempExtractDir = appDirUtils.getTmpDir() + "extract" + File.separator
        + UUID.randomUUID().toString() + File.separator;
    File extractDir = new File(tempExtractDir);
    if (!extractDir.exists() && !extractDir.mkdirs()) {
      throw SysException.of("Failed to create extract directory: " + tempExtractDir);
    }

    try {
      // 保存压缩包到临时文件
      File tempArchiveFile = new File(tempExtractDir + archiveFile.getOriginalFilename());
      archiveFile.transferTo(tempArchiveFile);

      // 解压压缩包
      String archiveFileName = archiveFile.getOriginalFilename().toLowerCase();
      if (archiveFileName.endsWith(".zip")) {
        extractZip(tempArchiveFile, extractDir);
      } else if (archiveFileName.endsWith(".tar.gz") || archiveFileName.endsWith(".tgz")) {
        extractTarGz(tempArchiveFile, extractDir);
      } else if (archiveFileName.endsWith(".tar")) {
        extractTar(tempArchiveFile, extractDir);
      } else {
        throw ProtocolException.of("Unsupported archive format: " + archiveFileName);
      }

      // 遍历解压后的文件结构并上传
      return processExtractedFiles(extractDir, isNewSpace, bizConfigDb, spaceDb,
          parentDirectoryDb, objectClient);
    } finally {
      // 清理临时文件
      try {
        FileUtils.deleteDirectory(extractDir);
      } catch (IOException e) {
        log.warn("Failed to delete extract directory: {}", tempExtractDir, e);
      }
    }
  }

  /**
   * 解压 ZIP 文件
   */
  private void extractZip(File zipFile, File extractDir) throws IOException {
    try (FileInputStream fis = new FileInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(fis)) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        File file = new File(extractDir, entry.getName());
        if (entry.isDirectory()) {
          file.mkdirs();
        } else {
          file.getParentFile().mkdirs();
          // 使用 try-with-resources 确保输出流正确关闭
          try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
            fos.flush();
          }
        }
        zis.closeEntry();
      }
    }
  }

  /**
   * 解压 TAR.GZ 文件
   */
  private void extractTarGz(File tarGzFile, File extractDir) throws IOException {
    try (FileInputStream fis = new FileInputStream(tarGzFile);
        GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
        TarArchiveInputStream tis = new TarArchiveInputStream(gzis)) {
      TarArchiveEntry entry;
      while ((entry = (TarArchiveEntry) tis.getNextEntry()) != null) {
        File file = new File(extractDir, entry.getName());
        if (entry.isDirectory()) {
          file.mkdirs();
        } else {
          file.getParentFile().mkdirs();
          // 使用 try-with-resources 确保输出流正确关闭
          try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = tis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
            fos.flush();
          }
        }
      }
    }
  }

  /**
   * 解压 TAR 文件
   */
  private void extractTar(File tarFile, File extractDir) throws IOException {
    try (FileInputStream fis = new FileInputStream(tarFile);
        TarArchiveInputStream tis = new TarArchiveInputStream(fis)) {
      TarArchiveEntry entry;
      while ((entry = (TarArchiveEntry) tis.getNextEntry()) != null) {
        File file = new File(extractDir, entry.getName());
        if (entry.isDirectory()) {
          file.mkdirs();
        } else {
          file.getParentFile().mkdirs();
          // 使用 try-with-resources 确保输出流正确关闭
          try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = tis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
            fos.flush();
          }
        }
      }
    }
  }

  /**
   * 处理解压后的文件，创建目录和上传文件
   */
  private List<ObjectFile> processExtractedFiles(File extractDir, boolean isNewSpace,
      BucketBizConfig bizConfigDb, Space spaceDb, SpaceObject parentDirectoryDb,
      ObjectClient objectClient) throws IOException {
    List<ObjectFile> uploadedFiles = new ArrayList<>();
    Map<String, SpaceObject> directoryMap = new HashMap<>(); // 路径 -> SpaceObject 映射
    Map<String, Long> directoryIdMap = new HashMap<>(); // 路径 -> 目录ID 映射

    // 获取根目录下的第一级内容
    File[] rootFiles = extractDir.listFiles();
    if (rootFiles == null || rootFiles.length == 0) {
      return uploadedFiles;
    }

    // 确定目标父目录
    Long targetParentDirId = nonNull(parentDirectoryDb) ? parentDirectoryDb.getId()
        : DEFAULT_ROOT_PID;
    SpaceObject targetParentDir = parentDirectoryDb;

    // 如果是新空间，直接使用根目录；如果是旧空间，需要创建一级目录
    String firstLevelDirName = null;
    SpaceObject firstLevelDir = null;
    if (!isNewSpace && rootFiles.length == 1 && rootFiles[0].isDirectory()) {
      // 旧空间且只有一个一级目录，创建该目录
      firstLevelDirName = sanitizeFileName(rootFiles[0].getName());
      firstLevelDir = createDirectory(firstLevelDirName, targetParentDirId, targetParentDir,
          bizConfigDb, spaceDb, objectClient);
      targetParentDirId = firstLevelDir.getId();
      targetParentDir = firstLevelDir;
      directoryMap.put(rootFiles[0].getAbsolutePath(), firstLevelDir);
      directoryIdMap.put(rootFiles[0].getAbsolutePath(), firstLevelDir.getId());
    }

    // 遍历所有文件，按层级顺序处理（先处理目录，再处理文件）
    List<File> allFiles = getAllFiles(extractDir);
    // 先创建所有目录
    for (File file : allFiles) {
      if (file.isDirectory()) {
        String relativePath = getRelativePath(extractDir, file);
        if (isNewSpace && relativePath.isEmpty()) {
          // 新空间的根目录，跳过
          continue;
        }
        if (!isNewSpace && firstLevelDir != null
            && relativePath.equals(firstLevelDirName)) {
          // 已创建的一级目录，跳过
          continue;
        }
        createDirectoryRecursive(file, extractDir, targetParentDirId, targetParentDir,
            directoryMap, directoryIdMap, isNewSpace, firstLevelDirName, bizConfigDb, spaceDb,
            objectClient);
      }
    }

    // 再上传所有文件
    for (File file : allFiles) {
      if (file.isFile()) {
        String parentPath = file.getParentFile().getAbsolutePath();
        SpaceObject parentDir = directoryMap.get(parentPath);
        Long parentDirId = parentDir != null ? parentDir.getId()
            : (directoryIdMap.get(parentPath) != null ? directoryIdMap.get(parentPath)
                : targetParentDirId);
        SpaceObject parentDirObj = parentDir != null ? parentDir
            : (nonNull(parentDirId) && !parentDirId.equals(DEFAULT_ROOT_PID)
                ? spaceObjectQuery.checkAndFind(parentDirId) : targetParentDir);

        ObjectFile uploadedFile = uploadFile(file, parentDirId, parentDirObj, bizConfigDb,
            spaceDb, objectClient);
        if (uploadedFile != null) {
          uploadedFiles.add(uploadedFile);
        }
      }
    }

    return uploadedFiles;
  }

  /**
   * 递归创建目录
   */
  private void createDirectoryRecursive(File dir, File extractRoot, Long baseParentDirId,
      SpaceObject baseParentDir, Map<String, SpaceObject> directoryMap,
      Map<String, Long> directoryIdMap, boolean isNewSpace, String firstLevelDirName,
      BucketBizConfig bizConfigDb, Space spaceDb, ObjectClient objectClient) throws IOException {
    String relativePath = getRelativePath(extractRoot, dir);
    if (isNewSpace && relativePath.isEmpty()) {
      return; // 新空间的根目录，跳过
    }
    if (!isNewSpace && firstLevelDirName != null && relativePath.equals(firstLevelDirName)) {
      return; // 已创建的一级目录，跳过
    }

    // 检查目录是否已创建
    if (directoryMap.containsKey(dir.getAbsolutePath())) {
      return;
    }

    // 获取父目录
    File parentFile = dir.getParentFile();
    Long parentDirId = baseParentDirId;
    SpaceObject parentDir = baseParentDir;

    if (parentFile != null && !parentFile.equals(extractRoot)) {
      String parentPath = parentFile.getAbsolutePath();
      if (directoryMap.containsKey(parentPath)) {
        parentDir = directoryMap.get(parentPath);
        parentDirId = parentDir.getId();
      } else if (directoryIdMap.containsKey(parentPath)) {
        parentDirId = directoryIdMap.get(parentPath);
        parentDir = spaceObjectQuery.checkAndFind(parentDirId);
      } else {
        // 递归创建父目录
        createDirectoryRecursive(parentFile, extractRoot, baseParentDirId, baseParentDir,
            directoryMap, directoryIdMap, isNewSpace, firstLevelDirName, bizConfigDb, spaceDb,
            objectClient);
        if (directoryMap.containsKey(parentPath)) {
          parentDir = directoryMap.get(parentPath);
          parentDirId = parentDir.getId();
        }
      }
    }

    // 创建目录
    String dirName = sanitizeFileName(dir.getName());
    SpaceObject createdDir = createDirectory(dirName, parentDirId, parentDir, bizConfigDb,
        spaceDb, objectClient);
    directoryMap.put(dir.getAbsolutePath(), createdDir);
    directoryIdMap.put(dir.getAbsolutePath(), createdDir.getId());
  }

  /**
   * 创建目录
   */
  private SpaceObject createDirectory(String dirName, Long parentDirId, SpaceObject parentDir,
      BucketBizConfig bizConfigDb, Space spaceDb, ObjectClient objectClient) {
    SpaceObject directory = new SpaceObject()
        .setId(uidGenerator.getUID())
        .setProjectId(nonNull(spaceDb.getProjectId()) ? spaceDb.getProjectId() : -1L)
        .setName(dirName)
        .setType(FileType.DIRECTORY)
        .setStoreType(objectClient.getStoreType())
        .setLevel(nonNull(parentDir) ? parentDir.getLevel() + 1 : 1)
        .setSize(0L)
        .setSpaceId(spaceDb.getId())
        .setParentDirectoryId(parentDirId)
        .setParentLikeId(nonNull(parentDir) ? (
            isNotEmpty(parentDir.getParentLikeId())
                ? parentDir.getParentLikeId() + "-" + parentDir.getId()
                : String.valueOf(parentDir.getId())
        ) : "");

    directory.setTenantId(getOptTenantId());
    directory.setCreatedBy(getUserId()).setCreatedDate(java.time.LocalDateTime.now())
        .setLastModifiedBy(getUserId()).setLastModifiedDate(java.time.LocalDateTime.now());

    // 检查目录名是否已存在
    try {
      spaceObjectQuery.checkAddDirectoryNameExists(spaceDb.getId(), parentDirId, dirName);
    } catch (Exception e) {
      // 如果目录已存在，尝试查找现有目录
      int count = spaceObjectRepo.countBySpaceIdAndParentDirectoryIdAndNameAndType(
          spaceDb.getId(), parentDirId, dirName, FileType.DIRECTORY);
      if (count > 0) {
        // 查找现有目录
        List<SpaceObject> existingDirs = spaceObjectRepo.findByParentDirectoryIdAndNameAndType(
            parentDirId, dirName, FileType.DIRECTORY.name());
        if (!existingDirs.isEmpty()) {
          return existingDirs.get(0);
        }
      }
      throw e;
    }

    // 保存目录
    spaceObjectRepo.save(directory);
    return directory;
  }

  /**
   * 上传文件
   */
  private ObjectFile uploadFile(File file, Long parentDirId, SpaceObject parentDir,
      BucketBizConfig bizConfigDb, Space spaceDb, ObjectClient objectClient) throws IOException {
    String fileName = sanitizeFileName(file.getName());
    Long fid = uidGenerator.getUID();
    Long oid = uidGenerator.getUID();
    String uniqueName = getBaseName(fileName) + "." + fid + "." + getExtension(fileName);
    String objectName = objectClient.getObjectName(bizConfigDb.getBucketName(),
        bizConfigDb.getBizKey(), getTenantId(), spaceDb.getId(), uniqueName);
    String publicToken = bizConfigDb.getPublicTokenAuth()
        ? RandomStringUtils.randomAlphanumeric(MAX_PUBLIC_TOKEN_LENGTH) : null;
    String downloadUrl = objectFileQuery.assembleDownloadUrl(fid, fileName,
        bizConfigDb, publicToken);
    String storeAddress = objectClient
        .getObjectUrl(bizConfigDb.getBucketName(), objectName);

    int level = nonNull(parentDir) ? parentDir.getLevel() + 1 : 1;
    String parentLikeId = nonNull(parentDir)
        ? (isNotEmpty(parentDir.getParentLikeId())
        ? parentDir.getParentLikeId() + "-" + parentDir.getId()
        : String.valueOf(parentDir.getId()))
        : "";

    // 读取文件内容到字节数组，避免流关闭问题
    byte[] fileContent = FileUtils.readFileToByteArray(file);
    String contentType = Files.probeContentType(file.toPath());
    MultipartFile multipartFile = new MockMultipartFile(fileName, fileName,
        contentType, fileContent);

    SpaceObject spaceObject = toUpdateSpaceObject(multipartFile, fileName, fid, oid, spaceDb,
        parentDirId, level, parentLikeId, objectClient.getStoreType());
    ObjectFile objectFile = toUploadObjectFile(multipartFile, fileName, objectName,
        uniqueName, downloadUrl, fid, oid, storeAddress, spaceDb, publicToken,
        objectClient.getStoreType(), bizConfigDb, parentDirId, applicationInfo);

    try {
      objectClient.putObject(bizConfigDb.getBucketName(), objectName,
          multipartFile.getInputStream(), AccessControl.Private);
      spaceObjectRepo.save(spaceObject);
      batchInsert0(singletonList(objectFile));
      return objectFile;
    } catch (Exception e) {
      log.error("Upload file {} error", fileName, e);
      throw SysException.of(format("Upload file %s error: %s", fileName, e.getMessage()));
    }
  }

  /**
   * 获取所有文件（包括目录）
   */
  private List<File> getAllFiles(File root) {
    List<File> files = new ArrayList<>();
    getAllFilesRecursive(root, files);
    return files;
  }

  /**
   * 递归获取所有文件
   */
  private void getAllFilesRecursive(File dir, List<File> files) {
    File[] children = dir.listFiles();
    if (children != null) {
      for (File child : children) {
        files.add(child);
        if (child.isDirectory()) {
          getAllFilesRecursive(child, files);
        }
      }
    }
  }

  /**
   * 获取相对路径
   */
  private String getRelativePath(File root, File file) {
    try {
      Path rootPath = Paths.get(root.getAbsolutePath());
      Path filePath = Paths.get(file.getAbsolutePath());
      return rootPath.relativize(filePath).toString().replace("\\", "/");
    } catch (Exception e) {
      return file.getName();
    }
  }

  @Override
  protected BaseRepository<ObjectFile, Long> getRepository() {
    return this.objectFileRepo;
  }

}
