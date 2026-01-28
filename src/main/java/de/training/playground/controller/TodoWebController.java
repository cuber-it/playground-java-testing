package de.training.playground.controller;

import de.training.playground.entity.Todo;
import de.training.playground.service.TodoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web-Controller fuer die Thymeleaf-Oberflaeche.
 *
 * <p>Stellt HTML-Seiten fuer die Todo-Verwaltung bereit.
 * Verwendet POST-Redirect-GET Pattern fuer Formulare.
 */
@Controller
@RequestMapping("/")
public class TodoWebController {

    private final TodoService service;

    public TodoWebController(TodoService service) {
        this.service = service;
    }

    /**
     * Zeigt die Hauptseite mit Todo-Liste.
     *
     * @param q optionaler Suchbegriff
     * @param model das Thymeleaf-Model
     * @return View-Name "index"
     */
    @GetMapping
    public String index(@RequestParam(required = false) String q, Model model) {
        List<Todo> todos = (q != null && !q.isBlank())
            ? service.search(q)
            : service.findAll();
        model.addAttribute("todos", todos);
        model.addAttribute("newTodo", new Todo());
        model.addAttribute("searchQuery", q);
        return "index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Todo todo) {
        service.save(todo);
        return "redirect:/";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Todo todo = service.findById(id).orElseThrow();
        model.addAttribute("todo", todo);
        return "edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Todo todo) {
        Todo existing = service.findById(id).orElseThrow();
        existing.setTitle(todo.getTitle());
        existing.setDescription(todo.getDescription());
        existing.setDueDate(todo.getDueDate());
        service.save(existing);
        return "redirect:/";
    }

    @PostMapping("/{id}/done")
    public String markDone(@PathVariable Long id) {
        service.markDone(id);
        return "redirect:/";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/";
    }

    /**
     * Exportiert alle Todos als CSV-Datei.
     *
     * @return CSV-Download mit Semikolon als Trennzeichen
     */
    @GetMapping("/export")
    public ResponseEntity<String> export() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Titel;Beschreibung;Faellig;Erledigt\n");
        for (Todo t : service.findAll()) {
            csv.append(t.getId()).append(";")
               .append(t.getTitle()).append(";")
               .append(t.getDescription() != null ? t.getDescription() : "").append(";")
               .append(t.getDueDate() != null ? t.getDueDate() : "").append(";")
               .append(t.isDone() ? "Ja" : "Nein").append("\n");
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=todos.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv.toString());
    }
}
