package com.ztp.ztpproject;

import com.ztp.ztpproject.builder.RaportDirector;
import com.ztp.ztpproject.command.AddTagCommand;
import com.ztp.ztpproject.command.ChangeContentCommand;
import com.ztp.ztpproject.command.ChangeNameCommand;
import com.ztp.ztpproject.command.CommandManager;
import com.ztp.ztpproject.flyweight.CategoryFactory;
import com.ztp.ztpproject.memento.NoteCaretaker;
import com.ztp.ztpproject.models.*;
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

        // try {
        //     System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        // } catch (UnsupportedEncodingException e) {
        //     throw new InternalError("VM does not support mandatory encoding UTF-8");
        // }
        // PrintWriter consoleOut = new PrintWriter(new OutputStreamWriter(System.out, "Cp850"));
        // consoleOut.println(filename);
        System.out.println("\n\n================== TESTY PROJEKTU ZTP ==================\n");

        // Note notePrototype = new Note("Prototype note", "TestNote",
        // Arrays.asList("TestWażne"));
        // Template noteTemplate = new Template(notePrototype);
        // Note customNote = (Note) noteTemplate.CloneCustomPrototype("Custom note",
        // "Custom note content");
        // customNote.showDetails();
        // Task defaultTask = (Task) taskTemplate.CloneDefaultPrototype();
        // defaultTask.showDetails();
        // Note defaultNote = (Note) noteTemplate.CloneDefaultPrototype();
        // defaultNote.showDetails();
        // // Test TagFactory
        // TagFactory tagFactory = new TagFactory();
        // Tag urgentTag1 = tagFactory.getState("Urgent");
        // Tag urgentTag2 = tagFactory.getState("Urgent");
        // System.out.println("Czy Tag 'Urgent' jest współdzielony: " + (urgentTag1 ==
        // urgentTag2)); // true
        // System.out.println("Nieistniejący Tag: " +
        // tagFactory.getStateDontAdd("Optional")); // null
        // System.out.println("Wszystkie tagi: " + tagFactory.getAllStates());
        /**
         * ten kod testuje user,role, task(category (factory i proxy)),*
         */
        // raportDirector(txtBuilder), template(ElementPrototype)
        // do dodania do testów note(template from note, command, memento, tag) -
        // wcześniejsze wywołania części można znaleźć wyżej
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 10);
        Date date = calendar.getTime();
        calendar.set(2025, Calendar.JANUARY, 12);
        Date date2 = calendar.getTime();
        calendar.set(2025, Calendar.JANUARY, 16);
        Date date3 = calendar.getTime();

        CategoryFactory categoryFactory = CategoryFactory.getInstance();

        User userN = new User("Nikodem", RoleList.ADMIN);
        User userM = new User("Marcin", RoleList.MODERATOR);
        User userD = new User("Damian", RoleList.USER);

        userN.addTask("Zadanie 1", "Opis zadania 1", 1, date, Arrays.asList("Work", "Personal"));
        System.out.println("Wszystkie Kategorie: " + categoryFactory.getAllStates());
        userM.addTask("Zadanie 2", "Opis zadania 2", 2, date2, Arrays.asList("Work2", "Personal"));
        System.out.println("Wszystkie Kategorie: " + categoryFactory.getAllStates());
        userD.addTask("Zadanie 3", "Opis zadania 3", 3, date3, Arrays.asList("Work3", "Personal"));
        System.out.println("Wszystkie Kategorie: " + categoryFactory.getAllStates());

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
        // director.generateRaportPdf(userN.getTaskList(), date, date2);

        //memento jest używane jako ogólna historia notatki
        //natomiast command jest wykorzystywane do edycji notatki - i jego historia ogranicza się do operacji edycji
        userN.addNote("Notatka 1", "Opis notatki", Arrays.asList("Note Work"));
        NoteCaretaker noteCaretaker = userN.getNoteCareTaker(0);
        CommandManager<Note> commandManager = new CommandManager<>(noteCaretaker);
        commandManager.executeCommand(new ChangeNameCommand("Notatka 2"));
        commandManager.executeCommand(new AddTagCommand(userN.getTagFactory(), "Work2"));

        System.out.println("Notatki użytkownika " + userN.getName() + ": " + userN.getNotesList());

        // Testy Note (template, command, memento, tag)
        System.out.println("\n\n================== TESTY Note ==================\n");

        userN.addNote("Notatka 289", "Opis notatki 111", Arrays.asList("Note Work"));
        NoteCaretaker testNoteCaretaker = userN.getNoteCareTaker(1);
        System.out.println("Initial Note: " + userN.getNote(1));

        testNoteCaretaker.addMementoFromOriginator();

        CommandManager<Note> testNoteCommandManager = new CommandManager<>(testNoteCaretaker);

        testNoteCommandManager.executeCommand(new ChangeNameCommand("Notatka 2"));
        System.out.println("After ChangeNameCommand: " + userN.getNote(1));
        testNoteCaretaker.addMementoFromOriginator();

        testNoteCommandManager.executeCommand(new AddTagCommand(userN.getTagFactory(), "Work2"));
        System.out.println("After AddTagCommand: " + userN.getNote(1));
        testNoteCaretaker.addMementoFromOriginator();

        System.out.println("History in Caretaker:");
        for (int i = 0; i < 3; i++) {
            System.out.println("State " + i + ": " + testNoteCaretaker.getNoteMemento(i));
        }

        testNoteCaretaker.restoreFromMemento(1);
        System.out.println("Restored to initial state: " + userN.getNote(1));

        testNoteCommandManager.executeCommand(new ChangeContentCommand("Restored Content"));
        System.out.println("After ChangeContentCommand: " + userN.getNote(1));

        userN.saveAsTemplate(userN.getNoteCareTaker(1).getOriginator());
        userN.addNoteFromTemplate(0, "TemplateNote", "Template Content");
        System.out.println("Note from template: " + userN.getNote(1));

    }
}
