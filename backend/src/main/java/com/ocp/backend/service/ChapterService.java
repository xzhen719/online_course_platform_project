package com.ocp.backend.service;

import com.ocp.backend.dto.request.ChapterRequest;
import com.ocp.backend.dto.response.ChapterDto;
import com.ocp.backend.entity.Chapter;
import com.ocp.backend.entity.Course;
import com.ocp.backend.repository.ChapterRepository;
import com.ocp.backend.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    // 課程詳情介紹用(不含影片)
    @Transactional
    public List<ChapterDto> getPublicChapters(Long courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByChapterIndexAsc(courseId);
        return chapters.stream()
                .map(chapter -> mapToPublicChapter(chapter))
                .collect(Collectors.toList());
    }

    // 使用者 "課程學習"頁面的章節表(含影片)
    @Transactional
    public List<ChapterDto> getAuthChapters(Long courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByChapterIndexAsc(courseId);
        return chapters.stream()
                .map(chapter -> mapToAuthChapter(chapter))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChapterDto addChapter(Long courseId, ChapterRequest request) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        Integer maxIndex = chapterRepository.findMaxChapterIndexByCourseId(course.getId()).orElse(0);
        int nextIndex = maxIndex + 1;

        Chapter chapter = new Chapter();
        chapter.setCourse(course);
        chapter.setTitle(request.getTitle());
        chapter.setSummary(request.getSummary());
        chapter.setDescription(request.getDescription());
        chapter.setDuration(request.getDuration());
        chapter.setVideoUrl(request.getVideoUrl());
        chapter.setChapterIndex(nextIndex);

        Chapter savedChapter = chapterRepository.save(chapter);

        int count = chapterRepository.countByCourseId(courseId);
        course.setTotalChapters(count);
        course.setTotalTime(course.getTotalTime() +
                (request.getDuration() == null ? 0 : request.getDuration()));
        courseRepository.save(course);
        return mapToAuthChapter(savedChapter);
    }

    @Transactional
    public ChapterDto updateChapter(Long chapterId, ChapterRequest request) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found: " + chapterId));

        Course course = chapter.getCourse();
        int oldDuration = chapter.getDuration() == null ? 0 : chapter.getDuration();
        int newDuration = request.getDuration() == null ? 0 : request.getDuration();

        chapter.setTitle(request.getTitle());
        chapter.setSummary(request.getSummary());
        chapter.setDescription(request.getDescription());
        chapter.setDuration(request.getDuration());
        chapter.setVideoUrl(request.getVideoUrl());

        Chapter savedChapter = chapterRepository.save(chapter);

        // Update course total time if duration changed
        if (oldDuration != newDuration) {
            course.setTotalTime(course.getTotalTime() - oldDuration + newDuration);
            courseRepository.save(course);
        }

        return mapToAuthChapter(savedChapter);
    }

    @Transactional(readOnly = true)
    public ChapterDto getChapterById(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found: " + chapterId));
        return mapToAuthChapter(chapter);
    }

    // public view: no videoUrl
    private ChapterDto mapToPublicChapter(Chapter chapter) {
        ChapterDto dto = new ChapterDto();
        dto.setId(chapter.getId());
        dto.setCourseId(chapter.getCourse().getId());
        dto.setIndex(chapter.getChapterIndex());
        dto.setTitle(chapter.getTitle());
        dto.setSummary(chapter.getSummary());
        dto.setDescription(chapter.getDescription());
        return dto;
    }

    // enrolled view: same as public + videoUrl
    private ChapterDto mapToAuthChapter(Chapter chapter) {
        ChapterDto dto = mapToPublicChapter(chapter);
        dto.setVideoUrl(chapter.getVideoUrl());
        return dto;
    }
}
