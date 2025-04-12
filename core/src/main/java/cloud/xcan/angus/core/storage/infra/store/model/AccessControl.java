package cloud.xcan.angus.core.storage.infra.store.model;

import cloud.xcan.angus.spec.ValueObject;
import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import lombok.Getter;


@Getter
@EndpointRegister
public enum AccessControl implements ValueObject<AccessControl>, EnumMessage<String> {
  /**
   * Specifies the owner is granted {@link Permission#FullControl}. No one else has access rights.
   * <p>
   * This is the default access control policy for any new buckets or objects.
   * </p>
   */
  Private("private"),

  /**
   * Specifies the owner is granted {@link Permission#FullControl} and the
   * {@link GroupGrantee#AllUsers} group grantee is granted {@link Permission#Read} access.
   * <p>
   * If this policy is used on an object, it can be read from a browser without authentication.
   * </p>
   */
  PublicRead("public-read"),

  /**
   * Specifies the owner is granted {@link Permission#FullControl} and the
   * {@link GroupGrantee#AllUsers} group grantee is granted {@link Permission#Read} and
   * {@link Permission#Write} access.
   * <p>
   * This access policy is not recommended for general use.
   * </p>
   */
  PublicReadWrite("public-read-write");

  private final String value;

  AccessControl(String value) {
    this.value = value;
  }

  @Override
  public boolean sameValueAs(AccessControl accessControl) {
    return this.getValue().equals(accessControl.getValue());
  }

  @Override
  public String getValue() {
    return this.value;
  }
}
