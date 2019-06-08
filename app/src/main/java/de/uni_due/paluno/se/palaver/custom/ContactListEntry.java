package de.uni_due.paluno.se.palaver.custom;

import java.util.Objects;

public class ContactListEntry {
    private String name;
    private String unread;

    public ContactListEntry(String name, String notification) {
        this.name = name;
        this.unread = notification;
    }

    public ContactListEntry() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactListEntry that = (ContactListEntry) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
