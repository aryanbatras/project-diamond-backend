# **India’s No. 1 Free & Open Source Student Driven University.**

<aside>
💎

### Why we are different?

> We are not just coding a platform. We are building a **student driven knowledge movement**.
Every small thing we do — code, video, UI, marketing — is **shaping that culture.**
>

> We are building a **zero-upfront cost SaaS-based e-learning platform** designed for college students.
The platform enables senior students to curate **exam-focused, career-oriented content.**
>

> Unlike traditional 80-100 hour lectures, our content is **short, targeted, and high-yield**.
We provide the best **peer-to-peer learning experience** with notes & resources provided by seniors.
>
</aside>

[Project Timeline](https://www.notion.so/26792c95bb6680f8a279e7a963bc6a6b?pvs=21)

[Official Team Members](https://www.notion.so/26792c95bb668019b46ed6c22fdee113?pvs=21)

[Requirements](https://www.notion.so/26792c95bb6680cbb364f0cc0037db4a?pvs=21)

[Strategy](https://www.notion.so/26792c95bb66807280badc140e937446?pvs=21)

[Technical Details](https://www.notion.so/26792c95bb66808db78edf3ffb7b93ff?pvs=21)

[Databases](https://www.notion.so/26792c95bb6680788abced3ed8ebb592?pvs=21)

# Development Flow Diagram

![diagram-export-7-9-2025-12_51_58-PM.png](attachment:30195f2e-461a-4017-adc7-d28c9cef3ea0:diagram-export-7-9-2025-12_51_58-PM.png)

## System Architecture

<aside>

### Basic Structure

**1. User**

- Roles: Visitor, Creator, Admin
- Auth: Firebase / Google OAuth
- User Info: FireStore [Email(ID), Password, Role, Profile-Info, Course-Track-Info]

**2. Courses**

- Course-Type: Academic, Career, FreeStuff
- Course-Info: MongoDb [ Course-Id, Course-Type, Title, Desc, Image-Link, Trailer-Link, Chapters: [Chapter-Id, ChapterTitle, MarkDownFile-Link, Video-Link ] ]
</aside>

## Requirements

<aside>

### Functional Requirements

- Users should be able to sign-up/login based on roles
- Users should be able to track their course progress
- Users can request access for becoming a creator
- Creators can upload a course. Update a course
- Creators can type & add images on the WYSIWYG editor
</aside>

<aside>

### Non-Functional Requirements

- Content should be loaded very fast [ Read Operations ]
- Content should be minimal & to the point
</aside>

## API Endpoints

### **Common API**

```
POST api/user : Body [ Email (Authenticated) ]
GET api/user/{Email}
GET api/courses/all [ Filters, Range ]
GET api/course/{CourseId}
GET api/course/{CourseId}/{ChapterId}
PUT api/user/{Email}/course-track : Body [ Course-Track-Info ]
POST api/request-creator : Body [ Email (Authenticated) ]
```

### **Creator's Exclusive API**

For Courses

```
POST api/course : Body [ Course-Type, Title, Desc, Image-Link, Trailer-Link ]
PUT api/course/{CourseId} : Body [ Course-Type, Title, Desc, Image-Link, Trailer-Link ]
```

For Chapters In Courses

```
POST api/course/{CourseId} : Body [ ChapterTitle, MarkDownFile-Link, Video-Link ]
PUT api/course/{CourseId}/{ChapterId} : Body [ ChapterTitle, MarkDownFile-Link, Video-Link ]
```

### **Admin's Exclusive API**

```
GET api/user/all
GET api/course/all
PUT api/user/{Email} : Body [ Role ]
```

<aside>
💎

api/user/{id}

</aside>

```json
{
  "id": "id_abc123",
  "email": "aryan@example.com",
  "displayName": "Aryan",
  "role": "visitor",
  "profile": {
    "college": "XYZ College",
    "year": "3",
    "avatarUrl": "https://..."
  },
  "courseTrack": {
    "course_001": {
      "completedChapters": 2,
      "totalChapters": 5,
      "lastSeenAt": "2025-09-18T..."
    }
  },
  "createdAt": "2025-09-18T10:00:00Z"
}
```

<aside>
💎

api/course/{courseId}

</aside>

```json
{
  "courseId": "course_001",
  "type": "Academic",
  "title": "High-Yield DSA",
  "slug": "high-yield-dsa",
  "description": "...",
  "imageUrl": "https://storage.googleapis.com/.../thumb.jpg",
  "trailerUrl": "https://.../trailer.mp4",
  "creatorId": "uid_abc123",
  "creatorSnapshot": {
    "uid": "uid_abc123",
    "displayName": "Aryan",
    "avatarUrl": "..."
  },
  "visibility": "PUBLIC",
  "publishStatus": "PUBLISHED",
  "createdAt": "2025-09-18T10:00:00Z",
  "updatedAt": "2025-09-18T11:00:00Z",
  "chapters": [
    {
      "chapterId": "ch_1",
      "title": "Intro",
      "markdownUrl": "gs://.../md1.md",
      "videoUrl": "gs://.../v1.mp4"
    },
    {
      "chapterId": "ch_2",
      "title": "Two-pointers",
      "markdownUrl": "gs://.../md2.md",
      "videoUrl": "gs://.../v2.mp4"
    }
  ]
}
```