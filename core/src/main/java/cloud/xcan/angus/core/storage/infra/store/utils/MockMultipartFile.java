package cloud.xcan.angus.core.storage.infra.store.utils;

import cloud.xcan.angus.spec.annotations.NonNullable;
import cloud.xcan.angus.spec.annotations.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class MockMultipartFile implements MultipartFile {

  private final String name;
  private final String originalFilename;
  private final String contentType;
  private final byte[] content;

  public MockMultipartFile(String name, @Nullable byte[] content) {
    this(name, "", (String) null, (byte[]) content);
  }

  public MockMultipartFile(String name, InputStream contentStream) throws IOException {
    this(name, "", (String) null, (byte[]) FileCopyUtils.copyToByteArray(contentStream));
  }

  public MockMultipartFile(String name, @Nullable String originalFilename,
      @Nullable String contentType, @Nullable byte[] content) {
    Assert.hasLength(name, "Name must not be empty");
    this.name = name;
    this.originalFilename = originalFilename != null ? originalFilename : "";
    this.contentType = contentType;
    this.content = content != null ? content : new byte[0];
  }

  public MockMultipartFile(String name, @Nullable String originalFilename,
      @Nullable String contentType, InputStream contentStream) throws IOException {
    this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
  }

  @Override
  @NotNull
  public String getName() {
    return this.name;
  }

  @Override
  @NonNullable
  public String getOriginalFilename() {
    return this.originalFilename;
  }

  @Override
  @Nullable
  public String getContentType() {
    return this.contentType;
  }

  @Override
  public boolean isEmpty() {
    return this.content.length == 0;
  }

  @Override
  public long getSize() {
    return (long) this.content.length;
  }

  @Override
  @NotNull
  public byte[] getBytes() throws IOException {
    return this.content;
  }

  @Override
  @NotNull
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(this.content);
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    FileCopyUtils.copy(this.content, dest);
  }
}
