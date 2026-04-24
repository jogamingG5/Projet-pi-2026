# 🚀 Frontend Generation Guide – Street League Angular App

**Project Date:** April 2026  
**Framework:** Angular (Standalone Components)  
**API Base URL:** `http://localhost:8081/streetleague/`  
**State Management:** Services + RxJS + State patterns  
**Styling:** Tailwind CSS  
**HTTP Client:** Angular HttpClient  

---

## 📋 Project Overview

This document serves as a comprehensive guide for AI agents and developers working on the **Street League** frontend. The application is an Angular-based SPA that interfaces with a Spring Boot backend API to manage sports matches and events.

### Architecture Stack
- **Backend:** Spring Boot REST API (Java) located in `/back` folder
- **Frontend:** Angular Standalone Components located in `/front/streetleague`
- **Database:** MongoDB (configured on backend)
- **HTTP Communication:** Angular HttpClient + Services pattern
- **UI/Styling:** Tailwind CSS (no component library)

---

## 📁 Project Structure

```
Projet-pi-2026/
├── back/                          # Spring Boot API
│   ├── src/main/java/com/example/projectPi/
│   │   ├── controllers/           # REST endpoints
│   │   ├── services/              # Business logic
│   │   ├── repositories/          # Data access (MongoDB)
│   │   ├── models/                # Domain entities
│   │   ├── dto/                   # Request/Response DTOs
│   │   ├── exception/             # Custom exceptions & handlers
│   │   ├── config/                # Configuration classes
│   │   └── middlewares/           # Filters & interceptors
│   └── pom.xml                    # Maven dependencies
│
├── front/streetleague/            # Angular Frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── app.routes.ts      # Route definitions
│   │   │   ├── app.ts            # Root component (init)
│   │   │   ├── app.config.ts     # Angular config
│   │   │   ├── services/         # HTTP & state services
│   │   │   ├── models/           # TypeScript interfaces
│   │   │   ├── pages/            # Page components (routed)
│   │   │   ├── components/       # Reusable components
│   │   │   ├── pipes/            # Custom pipes
│   │   │   └── utils/            # Utility functions
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css            # Global styles + Tailwind
│   ├── angular.json
│   ├── tsconfig.json
│   └── package.json
│
└── FRONTEND_GENERATION_GUIDE.md   # This file
```

---

## 🔌 Backend API Endpoints

### Base URL
```
http://localhost:8081/streetleague/api
```

### Available Endpoints

#### **Matches API**
```
GET    /matchs              # List all matches
POST   /matchs              # Create new match
GET    /matchs/:id          # Get match details
PUT    /matchs/:id          # Update match
DELETE /matchs/:id          # Delete match
```

**Match Request Body (Create/Update):**
```json
{
  "team1Id": "string",
  "team2Id": "string",
  "scoreTeam1": number,
  "scoreTeam2": number,
  "terrainId": "string",
  "date": "YYYY-MM-DD",
  "heure": "HH:mm",
  "sportId": "string",
  "status": "SCHEDULED | ONGOING | COMPLETED | CANCELLED",
  "type": "LEAGUE | FRIENDLY"
}
```

**Match Response (GET):**
```json
{
  "id": "string",
  "team1Id": "string",
  "team2Id": "string",
  "scoreTeam1": number,
  "scoreTeam2": number,
  "terrainId": "string",
  "date": "YYYY-MM-DD",
  "heure": "HH:mm",
  "sportId": "string",
  "status": "SCHEDULED | ONGOING | COMPLETED | CANCELLED",
  "type": "LEAGUE | FRIENDLY",
  "createdAt": "ISO8601",
  "updatedAt": "ISO8601"
}
```

---

#### **Events API**
```
GET    /events              # List all events
POST   /events              # Create new event
GET    /events/:id          # Get event details
PUT    /events/:id          # Update event
DELETE /events/:id          # Delete event
```

**Event Request Body (Create/Update):**
```json
{
  "nom": "string (required)",
  "description": "string",
  "dateDebut": "YYYY-MM-DD (required)",
  "dateFin": "YYYY-MM-DD (required, must be >= dateDebut)",
  "type": "LEAGUE | FRIENDLY",
  "sportId": "string",
  "teamsIds": ["string"]
}
```

**Event Response (GET):**
```json
{
  "id": "string",
  "nom": "string",
  "description": "string",
  "dateDebut": "YYYY-MM-DD",
  "dateFin": "YYYY-MM-DD",
  "type": "LEAGUE | FRIENDLY",
  "sportId": "string",
  "teamsIds": ["string"],
  "createdAt": "ISO8601",
  "updatedAt": "ISO8601"
}
```

---

## 🎯 Frontend Pages & Features

### 1️⃣ **Match List Page** (`/matches`)

**Route:** `/matches`  
**HTTP Calls:** `GET /api/matchs`

**UI Layout:**
- **Header with "+" button:** Opens Create Match Modal
- **Quick Filter Bar:** All | Scheduled | Ongoing | Completed | Cancelled
- **Responsive Table:**
  - Columns: Teams (team1Id vs team2Id) | Score | Date | Time | Status | Type | Actions
  - Rows show match data with status badges

**Styling Details:**
- Status badge colors:
  - `SCHEDULED` → blue (`bg-blue-500`)
  - `ONGOING` → green (`bg-green-500`)
  - `COMPLETED` → gray (`bg-gray-500`)
  - `CANCELLED` → red (`bg-red-500`)
- Type badge: `LEAGUE` → purple (`bg-purple-500`), `FRIENDLY` → amber (`bg-amber-500`)
- Action buttons per row: **View** (→ detail page), **Edit** (modal), **Delete** (confirm)

**User Interactions:**
- Click "View" → Navigate to `/matches/:id` detail page
- Click "Edit" → Open Create/Edit Modal with pre-filled data
- Click "Delete" → Show confirmation dialog, call `DELETE /api/matchs/:id`
- Click "+ New Match" → Open empty Create Modal

---

### 2️⃣ **Match Create/Edit Modal**

**Opened from:** Match List page or Match Detail page

**Form Fields:**
```
[Text Input]     Team 1 ID
[Text Input]     Team 2 ID
[Number Input]   Score Team 1 (default: 0, min: 0)
[Number Input]   Score Team 2 (default: 0, min: 0)
[Text Input]     Terrain ID
[Date Picker]    Date (required)
[Time Input]     Heure (Time, required)
[Text Input]     Sport ID
[Select]         Status (SCHEDULED, ONGOING, COMPLETED, CANCELLED)
[Select]         Type (LEAGUE, FRIENDLY)
[Button]         Cancel | Save
```

**Validation:**
- All fields required except Score (defaults to 0)
- Date must be a valid date
- Show inline error messages for field-level validation

**API Calls:**
- **Create:** `POST /api/matchs` (form data)
- **Update:** `PUT /api/matchs/:id` (form data)

**Error Handling:**
- If 400 error: Display field-specific error messages
- If 500 error: Show error toast "Erreur serveur, réessayez plus tard."
- Show success toast on save: "Match saved successfully"

**UX Enhancements:**
- Modal closes on successful save
- Show loading spinner while submitting
- Disable Save button while loading

---

### 3️⃣ **Event List Page** (`/events`)

**Route:** `/events`  
**HTTP Calls:** `GET /api/events`

**UI Layout:**
- **Header with "+" button:** Opens Create Event Modal
- **Card Grid Layout:** 
  - Desktop: 3 columns
  - Mobile: 1 column
  - Auto-responsive with Tailwind

**Card Content Per Event:**
```
┌─────────────────────────────────────┐
│ Event Name                          │
│ [Type Badge]  Sport ID              │
│                                     │
│ Date Début → Date Fin               │
│ Teams: N count                      │
│                                     │
│ Description (truncated 2 lines)     │
│ ...                                 │
│                                     │
│  [Edit] [Delete]                    │
└─────────────────────────────────────┘
```

**Styling Details:**
- Type badge: `LEAGUE` → purple, `FRIENDLY` → amber
- Description text: truncate to 2 lines with CSS `line-clamp-2`
- Hover effect on cards (subtle shadow/scale transform)

**User Interactions:**
- Click card → Navigate to `/events/:id` detail page
- Click "Edit" → Open Create/Edit Modal with pre-filled data
- Click "Delete" → Show confirmation dialog, call `DELETE /api/events/:id`
- Click "+ New Event" → Open empty Create Modal

---

### 4️⃣ **Event Create/Edit Modal**

**Opened from:** Event List page or Event Detail page

**Form Fields:**
```
[Text Input]        Nom de l'événement (required)
[Textarea]          Description
[Date Picker]       Date début (required)
[Date Picker]       Date fin (required)
[Select]            Type (LEAGUE, FRIENDLY)
[Text Input]        Sport ID
[Tag Input]         Teams IDs (type ID + Enter to add, click × to remove)
[Button]            Cancel | Save
```

**Validation:**
- `Nom` required
- `Date début` required
- `Date fin` required and must be ≥ `Date début`
- **Inline error:** "La date de fin doit être après la date de début" (if violated)
- Show field-level error messages

**API Calls:**
- **Create:** `POST /api/events` (form data)
- **Update:** `PUT /api/events/:id` (form data)

**Error Handling:**
- Same as Match Modal (see above)
- Show success toast: "Event created/updated successfully"

**Tag Input Component Details:**
- User types a Team ID
- Press Enter to add to list (show as tag with × close button)
- Click × to remove from list
- Send `teamsIds` as array in request body

---

### 5️⃣ **Event Detail Page** (`/events/:id`)

**Route:** `/events/:id`  
**HTTP Calls:** 
- `GET /api/events/:id` (event details)
- `GET /api/matchs` (all matches, filtered client-side)

**UI Layout:**

```
┌─────────────────────────────────────────────┐
│ [← Back Button]                             │
│                                             │
│ Event: [Name]                               │
│ ─────────────────────────────────────────   │
│ Description                                 │
│ Type: [LEAGUE badge]  Sport: [sportId]      │
│ Dates: [dateDebut] → [dateFin]              │
│ Teams: [teamsIds count]                     │
│ [Edit] [Delete]                             │
│                                             │
│ ─────────────────────────────────────────   │
│ Matchs de cet événement                     │
│                                             │
│ [Table: Teams | Score | Date | Status]      │
│ [rows filtered by eventId]                  │
│                                             │
└─────────────────────────────────────────────┘
```

**Matches Table Details:**
- Fetch all matches with `GET /api/matchs`
- Filter client-side: match.eventId === event.id (⚠️ need to confirm backend returns eventId)
- Show columns: Teams, Score, Date, Status
- Display status badges with colors (see Match List page)

**User Interactions:**
- Click "← Back" → Navigate back to `/events` list
- Click "[Edit]" → Open Event Create/Edit Modal with pre-filled data
- Click "[Delete]" → Show confirmation, call `DELETE /api/events/:id` → redirect to `/events`
- Click table row → Optional: navigate to match detail page (can be added later)

---

## 🛠️ Frontend Architecture Guidelines

### Service Layer (`/app/services`)

All HTTP calls must be encapsulated in services following the **Service Pattern**:

**File: `match.service.ts`**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';

@Injectable({ providedIn: 'root' })
export class MatchService {
  private API_URL = 'http://localhost:8081/streetleague/api/matchs';

  constructor(private http: HttpClient) {}

  getMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(this.API_URL);
  }

  getMatch(id: string): Observable<Match> {
    return this.http.get<Match>(`${this.API_URL}/${id}`);
  }

  createMatch(match: Omit<Match, 'id'>): Observable<Match> {
    return this.http.post<Match>(this.API_URL, match);
  }

  updateMatch(id: string, match: Partial<Match>): Observable<Match> {
    return this.http.put<Match>(`${this.API_URL}/${id}`, match);
  }

  deleteMatch(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
```

**File: `event.service.ts`** — Same pattern for Events

### Models/Interfaces (`/app/models`)

**File: `match.model.ts`**
```typescript
export interface Match {
  id: string;
  team1Id: string;
  team2Id: string;
  scoreTeam1: number;
  scoreTeam2: number;
  terrainId: string;
  date: string; // YYYY-MM-DD
  heure: string; // HH:mm
  sportId: string;
  status: 'SCHEDULED' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';
  type: 'LEAGUE' | 'FRIENDLY';
  createdAt?: string;
  updatedAt?: string;
}

export type MatchStatus = Match['status'];
export type MatchType = Match['type'];
```

**File: `event.model.ts`** — Similar structure for Events

### Component Structure

**Standalone Components Pattern:**
```typescript
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatService } from './services/match.service';

@Component({
  selector: 'app-match-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.css']
})
export class MatchListComponent implements OnInit {
  matches: Match[] = [];
  loading = false;
  error: string | null = null;

  constructor(private matchService: MatchService) {}

  ngOnInit(): void {
    this.loadMatches();
  }

  loadMatches(): void {
    this.loading = true;
    this.matchService.getMatches().subscribe({
      next: (data) => {
        this.matches = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load matches';
        this.loading = false;
      }
    });
  }
}
```

### Routing Configuration (`app.routes.ts`)

```typescript
import { Routes } from '@angular/router';
import { MatchListComponent } from './pages/match-list/match-list.component';
import { MatchDetailComponent } from './pages/match-detail/match-detail.component';
import { EventListComponent } from './pages/event-list/event-list.component';
import { EventDetailComponent } from './pages/event-detail/event-detail.component';

export const routes: Routes = [
  { path: '', redirectTo: '/matches', pathMatch: 'full' },
  { path: 'matches', component: MatchListComponent },
  { path: 'matches/:id', component: MatchDetailComponent },
  { path: 'events', component: EventListComponent },
  { path: 'events/:id', component: EventDetailComponent },
  { path: '**', redirectTo: '/matches' } // Wildcard route
];
```

---

## 💅 Styling Standards

### Tailwind CSS Classes

**Color Palette:**
- Primary: Blue (`bg-blue-500`, `text-blue-600`)
- Success: Green (`bg-green-500`)
- Warning/Danger: Red (`bg-red-500`)
- Info: Gray (`bg-gray-500`)
- Secondary: Purple (`bg-purple-500`)
- Accent: Amber (`bg-amber-500`)

**Common Classes:**
```css
/* Buttons */
.btn-primary: bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600
.btn-secondary: bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400
.btn-danger: bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600

/* Cards */
.card: bg-white rounded-lg shadow-md p-4 hover:shadow-lg transition

/* Grid layouts */
.grid-3-cols: grid grid-cols-3 gap-4 md:grid-cols-2 sm:grid-cols-1

/* Spacing */
.px-4 py-2: Horizontal & vertical padding
.gap-4: Grid/flex gap
```

**Responsive Breakpoints:**
- `sm:` → 640px
- `md:` → 768px
- `lg:` → 1024px
- `xl:` → 1280px

---

## ⚠️ Error Handling Strategy

**Global Error Handling:**

1. **404 Not Found:**
   - Show centered message: "Resource not found"
   - Display back navigation button
   - Example: `/events/:id` doesn't exist

2. **400 Bad Request:**
   - Show field-level validation errors from API response
   - Example: "Teams IDs are not valid"

3. **500 Internal Server Error:**
   - Show red banner: "Erreur serveur, réessayez plus tard."
   - Provide retry button

4. **Loading States:**
   - Show spinner component while fetching data
   - Disable buttons while submitting forms

**Toast Notifications:**
- **Success:** Green toast for successful operations (create, update, delete)
- **Error:** Red toast for failures
- Auto-dismiss after 3 seconds

---

## 📌 Current Status & Next Steps

### ✅ Completed
- Backend Spring Boot API setup
- MongoDB integration
- API endpoint definitions
- Frontend Angular scaffolding with `ng new`

### 🔄 In Progress
- Frontend page components
- Service layer integration
- Modal dialogs implementation
- Form validation

### 📋 To Do (Priority Order)
1. Build **Match List Page** with table & filter bar
2. Build **Match Create/Edit Modal**
3. Build **Event List Page** with card grid
4. Build **Event Create/Edit Modal**
5. Build **Event Detail Page** with matches table
6. Implement global error handling & toasts
7. Add loading spinners
8. Style with Tailwind CSS
9. Test all CRUD operations
10. **Future:** JWT authentication (Phase 2)

---

## 🔐 Authentication (Future - Phase 2)

Currently **not implemented**. Planned for future iteration:
- JWT token-based authentication with Spring Security
- Login page at `/login`
- Protected routes requiring authentication
- Token storage in localStorage/sessionStorage
- HTTP interceptor for automatic token injection
- Logout functionality

---

## 📚 Key Technologies & Docs

- **Angular 18+:** https://angular.io/docs
- **Tailwind CSS:** https://tailwindcss.com/docs
- **RxJS Observables:** https://rxjs.dev/guide/observable
- **Angular HttpClient:** https://angular.io/guide/http
- **Standalone Components:** https://angular.io/guide/standalone-components
- **Signals (optional):** https://angular.io/guide/signals

---

## 🚨 Important Notes for AI Agents

### ⚠️ Configuration Issue (Clarification Needed)
- **Current Ambiguity:** Initial requirements mentioned React 18 + Vite, but the project is actually **Angular**.
- **Resolution:** This guide assumes **Angular Standalone Components**. Verify with team lead before implementing.
- **API Base URL:** All services should use `http://localhost:8081/streetleague/api` (not `/streetleague/` alone)

### Field Naming Consistency
- Backend: `heure` (French), Frontend should display as "Heure" in labels
- Backend: `scoreTeam1`, `scoreTeam2` (camelCase)
- Backend: `teamsIds` (array), Frontend: tag input component

### API Response Clarifications (To Be Confirmed)
- ❓ Does Match response include `eventId` field? (needed for Event Detail page filtering)
- ❓ Are timestamps in ISO 8601 format?
- ❓ Do API endpoints support filtering query params? (e.g., `/api/matchs?status=SCHEDULED`)
- ❓ Error response format? (e.g., `{ message: string, errors: {...} }`)

### Development Workflow
1. Start backend: `cd back && ./mvnw spring-boot:run`
2. Start frontend: `cd front/streetleague && ng serve`
3. Frontend runs on: `http://localhost:4200` (default Angular port)
4. Backend API on: `http://localhost:8081/streetleague/api`

---

## 📧 Contact & Questions

For clarifications on:
- Backend API contracts
- Database schema details
- Business logic requirements
- Design/UX specifications

Please refer to the team lead or create an issue in the project repository.

---

**Document Version:** 1.0  
**Last Updated:** April 10, 2026  
**Maintained By:** Street League Development Team
