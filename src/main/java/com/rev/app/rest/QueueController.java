package com.rev.app.rest;

import com.rev.app.entity.PlaybackQueue;
import com.rev.app.entity.Song;
import com.rev.app.repository.SongRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class QueueController {

    @Autowired
    private SongRepository songRepository;

    private PlaybackQueue getQueue(HttpSession session) {
        PlaybackQueue queue = (PlaybackQueue) session.getAttribute("queue");
        if (queue == null) {
            queue = new PlaybackQueue();
            session.setAttribute("queue", queue);
        }
        return queue;
    }

    @PostMapping("/queue/add/{songId}")
    public String addToQueue(@PathVariable Long songId, HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        PlaybackQueue queue = getQueue(session);
        if (!queue.getSongIds().contains(songId)) {
            queue.getSongIds().add(songId);
            redirectAttrs.addFlashAttribute("success", "Song added to queue.");
        }
        return "redirect:/queue";
    }

    @PostMapping("/queue/remove/{songId}")
    public String removeFromQueue(@PathVariable Long songId, HttpSession session) {
        PlaybackQueue queue = getQueue(session);
        int index = queue.getSongIds().indexOf(songId);
        if (index != -1) {
            queue.getSongIds().remove(songId);
            // Adjust current index if necessary
            if (index < queue.getCurrentIndex()) {
                queue.setCurrentIndex(queue.getCurrentIndex() - 1);
            } else if (index == queue.getCurrentIndex()) {
                // If removing the currently playing song, we might need to stop or play next,
                // but for now just let it be, the UI will adjust.
                // Alternatively, reset index if queue is empty.
                if (queue.getSongIds().isEmpty()) {
                    queue.setCurrentIndex(-1);
                } else if (queue.getCurrentIndex() >= queue.getSongIds().size()) {
                    queue.setCurrentIndex(0);
                }
            }
        }
        return "redirect:/queue";
    }

    @PostMapping("/queue/clear")
    public String clearQueue(HttpSession session) {
        PlaybackQueue queue = getQueue(session);
        queue.getSongIds().clear();
        queue.setCurrentIndex(-1);
        return "redirect:/queue";
    }

    @PostMapping("/queue/shuffle/toggle")
    public String toggleShuffle(HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        PlaybackQueue queue = getQueue(session);
        queue.setShuffleEnabled(!queue.isShuffleEnabled());

        if (queue.isShuffleEnabled()) {
            // We won't reorder the actual List<Long> necessarily, we can just randomly
            // access it during next/prev.
            // OR, to be simpler, shuffle the list, but we lose original order.
            // Best approach for simple queue: shuffle the list itself.
            if (!queue.getSongIds().isEmpty()) {
                Long currentSong = queue.getCurrentIndex() >= 0 && queue.getCurrentIndex() < queue.getSongIds().size()
                        ? queue.getSongIds().get(queue.getCurrentIndex())
                        : null;
                Collections.shuffle(queue.getSongIds());
                if (currentSong != null) {
                    queue.setCurrentIndex(queue.getSongIds().indexOf(currentSong));
                }
            }
        }
        return "redirect:/queue";
    }

    @PostMapping("/queue/repeat/set")
    public String setRepeatMode(@RequestParam PlaybackQueue.RepeatMode mode, HttpSession session) {
        PlaybackQueue queue = getQueue(session);
        queue.setRepeatMode(mode);
        return "redirect:/queue";
    }

    @GetMapping("/queue")
    public String viewQueue(HttpSession session, Model model) {
        PlaybackQueue queue = getQueue(session);
        List<Song> queuedSongs = new ArrayList<>();

        for (Long id : queue.getSongIds()) {
            Optional<Song> s = songRepository.findById(id);
            s.ifPresent(queuedSongs::add);
        }

        Song currentSong = null;
        if (queue.getCurrentIndex() >= 0 && queue.getCurrentIndex() < queue.getSongIds().size()) {
            Long currentId = queue.getSongIds().get(queue.getCurrentIndex());
            currentSong = songRepository.findById(currentId).orElse(null);
        }

        model.addAttribute("queue", queue);
        model.addAttribute("queuedSongs", queuedSongs);
        model.addAttribute("currentSong", currentSong);

        return "queue";
    }

    @GetMapping("/queue/next")
    public String playNext(HttpSession session) {
        PlaybackQueue queue = getQueue(session);
        if (queue.getSongIds().isEmpty())
            return "redirect:/dashboard";

        int nextIndex = queue.getCurrentIndex() + 1;
        if (nextIndex >= queue.getSongIds().size()) {
            if (queue.getRepeatMode() == PlaybackQueue.RepeatMode.ALL) {
                nextIndex = 0;
            } else {
                return "redirect:/queue";
            }
        }
        queue.setCurrentIndex(nextIndex);
        return "redirect:/songs/play/" + queue.getSongIds().get(nextIndex);
    }

    @GetMapping("/queue/prev")
    public String playPrev(HttpSession session) {
        PlaybackQueue queue = getQueue(session);
        if (queue.getSongIds().isEmpty())
            return "redirect:/dashboard";

        int prevIndex = queue.getCurrentIndex() - 1;
        if (prevIndex < 0) {
            if (queue.getRepeatMode() == PlaybackQueue.RepeatMode.ALL) {
                prevIndex = queue.getSongIds().size() - 1;
            } else {
                prevIndex = 0;
            }
        }
        queue.setCurrentIndex(prevIndex);
        return "redirect:/songs/play/" + queue.getSongIds().get(prevIndex);
    }

    @GetMapping("/queue/auto-next")
    public String autoNext(HttpSession session) {
        PlaybackQueue queue = getQueue(session);
        if (queue.getSongIds().isEmpty())
            return "redirect:/dashboard";

        if (queue.getRepeatMode() == PlaybackQueue.RepeatMode.ONE) {
            return "redirect:/songs/play/" + queue.getSongIds().get(queue.getCurrentIndex());
        }

        return playNext(session);
    }
}
