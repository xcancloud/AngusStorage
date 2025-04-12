package cloud.xcan.angus.core.storage.infra.store.model;

import cloud.xcan.angus.spec.experimental.Value;

public enum Command implements Value<String> {
  IMAGE_RESIZE("image/resize");

  private String value;

  Command(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return this.name();
  }

  public static Command of(String value) {
    for (Command command : Command.values()) {
      if (command.value.equalsIgnoreCase(value)) {
        return command;
      }
    }
    return null;
  }
}
