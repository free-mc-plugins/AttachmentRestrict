package com.ericlam.mc.attachment.restrict.main;

import java.util.List;

class RequireAttachment {
    private String attachment;
    private List<String> required;

    RequireAttachment(String attachment, List<String> required) {
        this.attachment = attachment;
        this.required = required;
    }

    String getAttachment() {
        return attachment;
    }

    List<String> getRequired() {
        return required;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) return false;
        return ((RequireAttachment) o).attachment.equals(this.attachment);
    }

    @Override
    public int hashCode() {
        return this.attachment.hashCode();
    }
}
