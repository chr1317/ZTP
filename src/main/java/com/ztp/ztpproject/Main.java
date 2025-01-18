package com.ztp.ztpproject;

import com.ztp.ztpproject.builder.RaportDirector;
import com.ztp.ztpproject.command.AddTagCommand;
import com.ztp.ztpproject.command.ChangeNameCommand;
import com.ztp.ztpproject.command.CommandManager;
import com.ztp.ztpproject.flyweight.*;
import com.ztp.ztpproject.memento.NoteCaretaker;
import com.ztp.ztpproject.memento.NoteMemento;
import com.ztp.ztpproject.models.*;
import com.ztp.ztpproject.prototype.Template;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String charsetName = "cp" + Charset.forName("UTF-8").name();

            try {
                System.setProperty("console.encoding", charsetName);
                System.setProperty("file.encoding", charsetName);
            } catch (Exception e) {
                System.err.println("Failed to set console encoding to UTF-8.");
            }

            System.out.println("Platform detected: Windows. Console set to UTF-8.");
        } else {
            System.out.println("Non-Windows platform detected. No changes needed.");
        }

        System.out.println("\n\n================== TESTY PROJEKTU ZTP ==================\n");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 10);
        Date date = calendar.getTime();
        calendar.set(2025, Calendar.JANUARY, 12);
        Date date2 = calendar.getTime();
        calendar.set(2025, Calendar.JANUARY, 16);
        Date date3 = calendar.getTime();

        User userN = new User("Nikodem", RoleList.ADMIN);
        User userM = new User("Marcin", RoleList.MODERATOR);
        User userD = new User("Damian", RoleList.USER);

        CategoryFactoryProxy categoryFactoryProxy = new CategoryFactoryProxy(userN);

        userN.addTask("Zadanie 1", "Opis zadania 1", 1, date, Arrays.asList("Work", "Personal"));
        System.out.println("Wszystkie Kategorie: " + categoryFactoryProxy.getAllStates());
        userM.addTask("Zadanie 2", "Opis zadania 2", 2, date2, Arrays.asList("Work2", "Personal"));
        System.out.println("Wszystkie Kategorie: " + categoryFactoryProxy.getAllStates());
        userD.addTask("Zadanie 3", "Opis zadania 3", 3, date3, Arrays.asList("Work3", "Personal"));
        System.out.println("Wszystkie Kategorie: " + categoryFactoryProxy.getAllStates());

        System.out.println("Zadania użytkownika " + userN.getName() + ": " + userN.getTaskList());
        userN.saveAsTemplate(userN.getTask(0));
        userN.addTaskFromTemplate(0, "Zadanie 4", "Opis zadania 4");
        System.out.println(
                "Zadania użytkownika " + userN.getName() + " dodane z szablonu zadania 0: " + userN.getTask(1));

        RaportDirector director = new RaportDirector("raport");
        calendar.set(2025, Calendar.JANUARY, 8);
        date = calendar.getTime();
        calendar.set(2025, Calendar.JANUARY, 15);
        date2 = calendar.getTime();
        director.generateRaportTxt(userN.getTaskList(), date, date2);

        userN.addNote("Notatka 1", "Opis notatki", Arrays.asList("Note Work"));
        NoteCaretaker noteCaretaker = userN.getNoteCareTaker(0);
        Note.ReadOnlyNote readOnlyNote = noteCaretaker.getReadOnlyOriginator();
        Note note = new Note(readOnlyNote.getName(), readOnlyNote.getContent(), readOnlyNote.getTags());

        // Save multiple states of the note
        noteCaretaker.addMementoFromOriginator();
        note.setContent("Zmiana treści notatki");
        noteCaretaker.addMementoFromOriginator();
        note.setContent("Kolejna zmiana treści notatki");
        noteCaretaker.addMementoFromOriginator();

        // Print current state
        System.out.println("Po zmianach: " + note);

        // Restore to previous states
        NoteMemento memento1 = noteCaretaker.getNoteMemento(1);
        note.restoreFromMemento(memento1);
        System.out.println("Po przywróceniu do stanu 1: " + note);

        NoteMemento memento0 = noteCaretaker.getNoteMemento(0);
        note.restoreFromMemento(memento0);
        System.out.println("Po przywróceniu do stanu 0: " + note);

        CommandManager<Note> commandManager = new CommandManager<>(noteCaretaker);
        commandManager.executeCommand(new ChangeNameCommand("Notatka 2"));
        commandManager.executeCommand(new AddTagCommand(userN.getTagFactory(), "Work2"));
        System.out.println("Notatki użytkownika " + userN.getName() + ": " + userN.getNotesList());

        System.out.println("\n\n================== TESTY DODATKOWE ==================\n");

        System.out.println("Test wzorca Prototype na przykładzie notatek:");
        Note prototypeNote = new Note("Prototype", "This is a prototype note", Arrays.asList());
        Template noteTemplate = new Template(prototypeNote);
        Note clonedNote = (Note) noteTemplate.CloneCustomPrototype("Cloned Note", "Content of cloned note");
        System.out.println("Oryginał: " + prototypeNote);
        System.out.println("Klon: " + clonedNote);

        System.out.println("\nTest wzorca Flyweight z Proxy:");
        TagFactory tagFactory = new TagFactory();
        Tag urgentTag1 = tagFactory.getState("Urgent");
        Tag urgentTag2 = tagFactory.getState("Urgent");
        System.out.println("Czy Tag 'Urgent' jest współdzielony: " + (urgentTag1 == urgentTag2));
        System.out.println("Nieistniejący Tag: " + tagFactory.getState("Optional"));
        System.out.println("Wszystkie tagi: " + tagFactory.getAllStates());

        System.out.println("\nTesty zakończone.");
    }
}
