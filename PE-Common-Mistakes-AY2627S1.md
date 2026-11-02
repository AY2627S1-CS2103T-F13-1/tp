# CS2103T Practical Exam (PE): Rules Analysis and the Most Common Egregious Mistakes in AY26/27 S1

> **Scope.** This report has two parts. The first summarises the official PE rules for AY26/27 Semester 1. The second ranks the serious bugs that AY26/27 S1 teams most often had in their own products, based on the bug reports those teams filed. Each bug is marked as either **commonly found** (testers will spot it in the first hour) or **hard to find** (only a tester who probes on purpose will see it).
>
> **Note on the data.** As of 2 Nov 2026 the PE (Week 13) has **not happened yet**, so no PE bug reports exist for this semester. The figures below come from the teams' own **alpha-test (`alpha-bug`) and beta-test (`beta-bug`)** reports. These reports were written by students playing the role of PE testers, so they are a good guide to what testers will find.

---

## Table of contents

1. [Data and method](#1-data-and-method)
2. [The PE rules that matter](#2-the-pe-rules-that-matter)
3. [Quick reference: most common egregious mistakes](#3-quick-reference-most-common-egregious-mistakes)
4. [Part A: Commonly found bugs (easy to discover)](#4-part-a-commonly-found-bugs-easy-to-discover)
5. [Part B: Bugs that are hard to find (need deliberate probing)](#5-part-b-bugs-that-are-hard-to-find-need-deliberate-probing)
6. [Documentation (UG/DG) mistakes](#6-documentation-ugdg-mistakes)
7. [Procedural mistakes during the PE](#7-procedural-mistakes-during-the-pe-these-lose-marks-even-without-a-bug)
8. [Pre-PE checklist](#8-pre-pe-checklist)

---

## 1. Data and method

| Item | Value |
|---|---|
| Rule source | AY26/27 S1 course website source (`NUS-CS2103-AY2627-S1/website`, files `admin/tp-pe-fragment.md`, `admin/tp-pe-bug-triaging-guidelines-fragment.md`, `admin/tp-grading-bugs-fragment.md`, `admin/tp-ped-fragment.md`, `admin/tp-constraints.md`, `admin/tp-tasks-fragment.md`), last updated 27 Oct 2026 |
| Bug corpus | **4,316 issues** labelled `alpha-bug`, `beta-bug` or `type.{FunctionalityBug,FeatureFlaw,DocumentationBug}`, created Aug to Nov 2026 |
| Teams covered | **114 team repos** (`AY2627S1-CS2103T-*`: 102, `AY2627S1-CS2103-*`: 12) |
| Severity labels present | `Low` 1,294 · `Medium` 470 · `VeryLow` 296 · `High` 66 (the rest have no severity label) |
| How issues were classified | Regex matching on issue titles for each bug category, then a manual check of samples. **The team counts are lower bounds**: many reports describe the same problem in words the regexes do not catch. |

**How to read "Teams".** This is the number of the 114 teams that filed **at least one** issue in that category. A category reported by more than about 40 teams is close to universal: expect almost every PE tester to probe it.

---

## 2. The PE rules that matter

### 2.1 Format and timeline

| Phase | When | What happens |
|---|---|---|
| **Phase 1: Bug reporting** | **Week 13, Fri 12:00 to Sat 12:00** (individual take-home exam; aim to finish by Fri 23:59) | Part I: test the product (~60 min). Part II: evaluate the UG and DG (~30 min). Part III: overall evaluation on TEAMMATES (~15 min; **automatic penalty if skipped**). Part IV: trim the list to **at most 6 bugs** and justify their severity. |
| **Phase 2: Developer response** | Sun to Tue after the PE (aim to finish by Mon 23:59) | The team responds in a private repo `pe-{teamID}`: assignees, `response.*`, `type.*`, `severity.*`, duplicates, and a `# T` justification comment. |
| **Phase 3: Tester response** | Wed to Fri after the PE (aim to finish by 6pm) | The tester objects with `# R` (response), `# S` (severity) or `# D` (duplicate), **one comment per objection**. |
| **Phase 4: Tutor moderation** | After Phase 3 | Tutors pick **either the tester's or the developer's position, never a middle ground**. |

- In scope: the **JAR**, the **UG HTML page** and the **DG HTML page**. Out of scope: README and other `.md` files, data and config files (unless they affect the app), terminal output (unless it alarms the user), and code quality.
- Testers may use automated tools and AI. Reporting a bug someone else found counts as **academic dishonesty**.
- Testers run the JAR with **Java 25** using `java -jar`, from an **empty folder**.

### 2.2 Bug types (counted in different grade components)

| Type | Definition | Typical examples |
|---|---|---|
| `type.FunctionalityBug` | The product behaves differently from the UG or from normal expectations; a legitimate user action is not handled (wrong commands, extra parameters); the error message does not match the error | crashes, wrong results, a wrong error message |
| `type.FeatureFlaw` | A v1.6 feature is incomplete or badly designed for the target user. Also covers hard-to-test features, features that do not fit the product, features not optimised for fast typists, and **project constraint violations** | overly strict validation, weak duplicate detection, wrong case sensitivity, vague error messages |
| `type.DocumentationBug` | A flaw in the UG or DG | UG/DG mismatch, AB3 leftovers, UML errors, broken links, typos |

### 2.3 Severity levels

| Severity | Definition (verbatim intent) |
|---|---|
| `VeryLow` | **Purely cosmetic** (typo, spacing, layout, colour, font). Only cosmetic problems get this label. |
| `Low` | Unlikely to affect normal operations; appears only in **very rare situations**; minor inconvenience. |
| `Medium` | **Occasional** inconvenience to some users, who can still use the product. |
| `High` | Makes the product **almost unusable for most users**. |

- Severity may also take into account the **damage to the product's credibility**. Obvious, visible bugs score higher.
- **When in doubt, choose the lower severity.** Tutors follow the same rule when moderating.

### 2.4 Developer `response.*` labels

| Label | Effect |
|---|---|
| `Accepted` | Counts against the team. Any bug that *should have been fixed in v1.6*. |
| `NotInScope` | No penalty for the developer; a small credit for the tester. **Prefer this over `NotApplicable`.** Not allowed for typos, for flaws that make the app barely usable, or for flaws with a better design that would have taken little extra effort. |
| `NotApplicable` | Only when the tester was clearly wrong. |
| `CannotReproduce` | Only after **several reasonable attempts**, including trying to work out the cause from screenshots or error messages. |
| `IssueUnclear` | The description is genuinely unclear. |

### 2.5 Scoring rules worth remembering

- Testers earn **3 marks** for finding bugs. **The best 5 of the 6 bugs they send count.**
- Bugs found in your product determine the **7 marks for QA** and also affect other components (documentation bugs reduce the documentation marks).
- Bugs are penalised only once your **bug density is above a threshold**. A `VeryLow` bug costs about **-0.15**, and a developer can usually have about 5 `VeryLow` bugs before any penalty applies.
- The penalty for a bug is **split equally among its assignees**. If no one is assigned, it is split across the whole team. If more than **50% of bugs are left unassigned**, a **20% tax** is added to every member's penalty.
- **Duplicates are not penalised twice for developers**, but each tester still earns credit, slightly reduced for "obvious" bugs that many testers reported.
- **Accuracy bonus:** if the final `severity.*` and `response.*` match what you chose, you earn about 10% to 20% more (both testers and developers). Unsuccessful objections lower your accuracy.
- **Bugs inherited from AB3 count.** Listing a bug under *Known issues* in the UG can lower its severity, but the bug still counts.

### 2.6 Related constraints (violations are `FeatureFlaw`s)

- **Feature freeze for v1.6:** no member may change more than **15% or 150 LoC (whichever is higher)** of their functional code in `src/main` (a 5% tolerance applies, so up to 20%). The penalty is **at least -2**.
- **Data sandbox:** the app must not read or write outside the folder that contains the JAR.
- **Human-editable file:** support for manual editing must be at least as good as AB3's.
- **Single JAR** that runs on Windows, Linux and **macOS (Intel and ARM)**.
- **Screen resolution:** the app must work well at 1920×1080 with 100% and 125% scaling, and be usable at **1280×720 with 150% scaling**.
- Java 25 only, no installer, no remote server, no DBMS.

### 2.7 How the triage rules define the most heavily punished mistakes

The triaging guidelines name several patterns as bugs outright. Testers read these guidelines, so they actively hunt for these patterns:

1. **Overzealous input validation** (`FeatureFlaw`). Example given: rejecting `s/o` in legal names.
2. **Weak duplicate detection** (`FeatureFlaw`). `John Doe` and `john  doe` are likely the same person.
3. **Wrong case sensitivity** (`FeatureFlaw`). Names, tags and search keywords should be case-insensitive.
4. **Vague error messages** (`FeatureFlaw`). Calling an invalid value a "format error" (or the reverse) is `Low`.
5. **Command formats that are long or hard to type** (`FeatureFlaw`).
6. **Search that is less useful than it could be**: case-sensitive search, or AND-only search (`FeatureFlaw`).
7. **User mistakes that crash the app, corrupt data or make the app unusable.** Example given: a missing space between parameters.
8. **Manual edits to the data file** handled worse than AB3 handles them.
9. **UG/DG errors:** UML notation errors are `Low` or higher, broken links are `Low` or `Medium`, typos are `VeryLow` and can never be `NotInScope`.

---

## 3. Quick reference: most common egregious mistakes

"Teams" counts the 114 AY26/27 S1 teams that recorded the problem in their own tracker. It is a lower bound.

| # | Mistake | Found by | Typical type / severity | Teams |
|---|---|---|---|---|
| A1 | Overly strict validation of names (apostrophes, hyphens, accents, `s/o`, `d/o`, `a/l`) | **Commonly found** | FeatureFlaw · Medium | 63+ (21 for `s/o`/`d/o` alone) |
| A2 | Vague or misleading error messages ("Invalid command format!" for everything) | **Commonly found** | FeatureFlaw · Low | 71+ |
| A3 | AB3 leftovers in the app (help URL, "AddressBook" wording, `addressbook.json`, AB3 screenshots) | **Commonly found** | Doc/Functionality · Low | 57+ |
| A4 | Phone and email validation either too strict (`+65`, spaces) or too loose (`+++123+++`) | **Commonly found** | FeatureFlaw · Low/Medium | 50+ email, 47+ phone |
| A5 | Find/search limits (whole-word only, AND vs OR, cannot search other fields) | **Commonly found** | FeatureFlaw · Low/Medium | 55+ |
| A6 | `clear` or delete with no confirmation and no undo; `clear 1` wipes everything | **Commonly found** | FeatureFlaw/Functionality · Medium | 45+ |
| A7 | Duplicate detection ignores case, whitespace or near-matches | **Commonly found** | FeatureFlaw · Medium | 30+ (63+ broader) |
| A8 | Wrong case sensitivity (tags, command words, emails) | **Commonly found** | FeatureFlaw · Low | 64+ |
| A9 | Long input truncates the UI; result box does not wrap | **Commonly found** | Functionality · VeryLow/Low | 37+ |
| A10 | Commands without parameters (`list`, `exit`, `help`, `clear`) accept extra arguments | **Commonly found** | Functionality · Low (Medium for `clear`) | 32+ |
| A11 | Unknown or mistyped prefix silently saved into another field | **Commonly found** | Functionality · Medium | 33+ |
| A12 | Large or zero index and integer overflow give the wrong error | **Commonly found** | Functionality · Low | 31+ |
| A13 | Editing clears the active filter, so indexes shift and the wrong item gets changed | **Commonly found** | Functionality · Medium | 18+ |
| B1 | **Corrupt or invalid data file loads as empty, then is overwritten by the next command** | Hard to find | Functionality · **High/Medium** | 40+ |
| B2 | `null` entries in the JSON or `preferences.json` crash the app at startup | Hard to find | Functionality · Medium/High | 53+ |
| B3 | Unicode whitespace, NBSP, full-width characters or pasted tabs/newlines bypass validation | Hard to find | Functionality/FeatureFlaw · Low | 54+ |
| B4 | Running a second instance silently overwrites data saved by the first | Hard to find | Functionality · **High/Medium** | 28+ |
| B5 | Read-only or write-protected folder: crash, or read-only commands fail | Hard to find | Functionality · Low/Medium | 34+ |
| B6 | Very long email causes a `StackOverflowError` (inherited from AB3's email regex) | Hard to find | Functionality · Low | 17+ |
| B7 | A failed save leaves the in-memory state changed; a retry deletes the wrong item | Hard to find | Functionality · Medium/High | 14+ |
| B8 | Cascade and stale references (renamed or deleted parent not updated in children) | Hard to find | Functionality · Medium/High | 11+ |
| B9 | Data and preference files written relative to the launch directory, not the JAR folder (data-sandbox constraint) | Hard to find | FeatureFlaw · Low/High | 11+ |
| B10 | Not usable at 1280×720 with 150% scaling (screen-resolution constraint) | Hard to find | FeatureFlaw · Low/Medium | 20+ |
| B11 | Personal data written to log files | Hard to find | FeatureFlaw · Low/Medium | 10+ |
| B12 | JAR does not run on ARM Macs (JavaFX natives missing) | Hard to find | FeatureFlaw · **High** | 5+ |
| B13 | Impossible or out-of-range dates accepted; valid formats rejected | Hard to find | Functionality · Low | 15+ |
| D1 | UG or DG contradicts the actual behaviour | **Commonly found** | Documentation · Low/Medium | 65+ |
| D2 | Template placeholders left in the DG (`{ more test cases … }`, unfinished Acknowledgements) | **Commonly found** | Documentation · Low | 28+ |
| D3 | Outdated UG screenshots (some still show the AB3 URL) | **Commonly found** | Documentation · VeryLow/Low | 26+ |
| D4 | UML diagrams wrong or missing classes | Moderately hard | Documentation · Low | 36+ |
| D5 | Problems with user stories, use cases, NFRs or the glossary | Moderately hard | Documentation · Low | 31+ |

---

## 4. Part A: Commonly found bugs (easy to discover)

These are the bugs testers find within the 60-minute product-testing part: they just try typical inputs from the UG plus a few obvious variations. Because so many testers report them, each report earns the tester **slightly less** (an "obvious" bug). The team is still penalised once for each accepted bug, and these bugs damage credibility badly.

### A1. Overly strict name validation (the textbook example)
- **What:** Names with `'`, `-`, `.`, `,`, accents (`Zoë`) or `s/o`, `d/o`, `a/l`, `a/p` are rejected or misparsed. A name containing `s/o` gets split into a different field.
- **Rule:** The triage guideline uses this exact case: *"disallowing `s/o` in a person name because `/` is used as a command delimiter can cause a major problem if the input is expected to match the legal name of the person."* It is listed under *Overzealous input validation*.
- **Severity:** usually `FeatureFlaw` `Medium`. It is hard to defend as `NotInScope` when the target users have such names (most apps for Singapore users).
- **Examples:** [W12-3#202 "Names containing s/o or d/o cannot be entered"](https://github.com/AY2627S1-CS2103T-W12-3/tp/issues/202), [F14a-3#108](https://github.com/AY2627S1-CS2103T-F14a-3/tp/issues/108), [T15-3 "find-s and find-g reject valid hyphenated/apostrophe names" (High)](https://github.com/AY2627S1-CS2103T-T15-3/tp/issues/121), [W08-1#219 "Names with accented letters, e.g. Zoë, are rejected"](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/219), [T14-3#146](https://github.com/AY2627S1-CS2103T-T14-3/tp/issues/146).
- **Fix:** Accept any printable characters except the exact prefix tokens preceded by a space. Warn instead of blocking. Document the limits in the UG.

### A2. Vague or misleading error messages
- **What:** The generic AB3 message "Invalid command format!" appears for a bad index, an unknown prefix or a bad value. Index `0` is reported as a format error. The error names the wrong field.
- **Rule:** *Specificity of error message*: `FeatureFlaw`. Mixing up "invalid value" and "wrong format" is `Low`.
- **Examples:** [T10-3#159](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/159), [CS2103-F12-2#200 "Unknown parameter gives a misleading name error"](https://github.com/AY2627S1-CS2103-F12-2/tp/issues/200), [W14-4#124](https://github.com/AY2627S1-CS2103T-W14-4/tp/issues/124), [T09-4#176 "Misleading view 0 error message"](https://github.com/AY2627S1-CS2103T-T09-4/tp/issues/176).

### A3. AB3 leftovers visible in the product
- **What:** The help window still links to the **AB3 user guide**. Messages still say "person" or "address book". The data file is still `addressbook.json`. The window title still says AddressBook. The release JAR name does not match the one in the UG.
- **Why it is egregious:** It is visible in the first minute, hurts credibility, and is trivial to fix, so it **cannot be `NotInScope`**.
- **Examples:** [CS2103-F10-1 help `USERGUIDE_URL` still points to AB3 (High)](https://github.com/AY2627S1-CS2103-F10-1/tp/issues/221), [W08-4#80](https://github.com/AY2627S1-CS2103T-W08-4/tp/issues/80), [W10-2#153](https://github.com/AY2627S1-CS2103T-W10-2/tp/issues/153), [T10-3#151 "UG Quick start uses addressbook.jar"](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/151), [W09-2#146](https://github.com/AY2627S1-CS2103T-W09-2/tp/issues/146).

### A4. Phone and email validation (too strict or too loose)
- **Too strict:** `+65 9123 4567`, `9123-4567` or a number copied from the app's own contact card is rejected. Valid emails such as Punycode domains, consecutive underscores or uppercase domains are rejected.
- **Too loose:** `+++123+++`, phone numbers of unlimited length, letters in phone numbers, invalid emails accepted.
- **Rule:** Overzealous validation is a `FeatureFlaw`, and so is missing handling of harmful input. Limiting phones to 8 digits is "not reasonable" unless the target users are guaranteed to have such numbers.
- **Examples:** [W08-3#134 "+65 rejected"](https://github.com/AY2627S1-CS2103T-W08-3/tp/issues/134), [F14a-2#114 "number copied from the client card is rejected"](https://github.com/AY2627S1-CS2103T-F14a-2/tp/issues/114), [W08-1#260 "+++123+++"](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/260), [F14a-4#117 "Phone accepts letters"](https://github.com/AY2627S1-CS2103T-F14a-4/tp/issues/117), [F08-1#126 Punycode](https://github.com/AY2627S1-CS2103T-F08-1/tp/issues/126).

### A5. Find/search is less useful than it could be
- **What:** Only whole words match (`find Mary` misses `Mary-Jane`). Adding a keyword widens the results when users expect it to narrow them (or the reverse, without documentation). The app cannot search by phone, tag or address. Search is case-sensitive.
- **Rule:** *A feature that is less useful than it could be*: `FeatureFlaw`.
- **Examples:** [CS2103-F10-1#155 "find only matches whole words"](https://github.com/AY2627S1-CS2103-F10-1/tp/issues/155), [W09-3#158](https://github.com/AY2627S1-CS2103T-W09-3/tp/issues/158), [W09-3#159](https://github.com/AY2627S1-CS2103T-W09-3/tp/issues/159), [W08-1#259](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/259).

### A6. Destructive commands with no safety net
- **What:** `clear` wipes all data with no confirmation and no undo. **`clear 1` or `clear abc` still wipes everything.** Clearing while viewing a filtered or archived list also deletes items that are not shown.
- **Why it is egregious:** A plausible typo causes **data loss**. The guidelines say user mistakes "should not … corrupt the data". This was labelled `High` in several trackers.
- **Examples:** [F08-3 "a mistyped `clear 1` wipes the register" (High)](https://github.com/AY2627S1-CS2103T-F08-3/tp/issues/218), [W08-4 "`clear` with extra input silently deletes every student" (High)](https://github.com/AY2627S1-CS2103T-W08-4/tp/issues/83), [F13-4 "clear while viewing Archives also deletes every active student" (High)](https://github.com/AY2627S1-CS2103T-F13-4/tp/issues/184), [W08-1#217](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/217), [CS2103-F10-1#153](https://github.com/AY2627S1-CS2103-F10-1/tp/issues/153).

### A7. Weak duplicate detection
- **What:** `John Doe`, `john doe` and `John  Doe` are accepted as three different people. Phone numbers that differ only in formatting (`9123 4567` and `91234567`) bypass the check. Alternatively, the check is too strict and blocks two real people who share a name.
- **Rule:** *Bugs related to duplicate detection*: `FeatureFlaw` unless the limitations are documented. Showing a warning is recommended.
- **Examples:** [W09-1#176](https://github.com/AY2627S1-CS2103T-W09-1/tp/issues/176), [W08-3#135](https://github.com/AY2627S1-CS2103T-W08-3/tp/issues/135), [F14a-4#118](https://github.com/AY2627S1-CS2103T-F14a-4/tp/issues/118), [W09-3#144](https://github.com/AY2627S1-CS2103T-W09-3/tp/issues/144), [T08-1#215 "Distinct patients with the same name cannot be recorded"](https://github.com/AY2627S1-CS2103T-T08-1/tp/issues/215).

### A8. Wrong case sensitivity
- **What:** `friends`, `Friends` and `FRIENDS` are stored as three tags. `LIST` gives "Unknown command". Flags are case-sensitive while everything else is not. Email domains are compared with case.
- **Rule:** *Case sensitivity should follow the real-world entity*: `FeatureFlaw`.
- **Examples:** [W08-3#139](https://github.com/AY2627S1-CS2103T-W08-3/tp/issues/139), [F09-2a#193](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/193), [T12-4#122 "Command words are case-sensitive"](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/122), [F08-2 "every flag is CASE SENSITIVE" (High)](https://github.com/AY2627S1-CS2103T-F08-2/tp/issues/191), [F10-4#114](https://github.com/AY2627S1-CS2103T-F10-4/tp/issues/114).

### A9. Long input breaks the UI
- **What:** Long names, tags or addresses are cut off. The result box shows only about 3 lines and does not wrap. Cards overflow.
- **Rule:** Cosmetic issues are `VeryLow`, but **`Low` or higher if they hinder the user** (for example, only the first few characters are shown).
- **Examples:** [T12-4#198](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/198), [T10-2#185](https://github.com/AY2627S1-CS2103T-T10-2/tp/issues/185), [W08-1#228](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/228), [T15-3 "Paid field gets cut off" (High)](https://github.com/AY2627S1-CS2103T-T15-3/tp/issues/128).

### A10. Extra arguments silently accepted
- **What:** `exit 123` exits, `list abc` lists and `help me` opens help. This is harmless except for **`clear xyz`, which deletes everything** (see A6). Some commands reject extra arguments and others accept them, which is inconsistent.
- **Rule:** *A legitimate user behaviour is not handled (e.g., incorrect commands, extra parameters)*: `FunctionalityBug`.
- **Examples:** [W09-3#164](https://github.com/AY2627S1-CS2103T-W09-3/tp/issues/164), [T12-4#136](https://github.com/AY2627S1-CS2103T-T12-4/tp/issues/136), [W12-3#179](https://github.com/AY2627S1-CS2103T-W12-3/tp/issues/179), [W10-3#129](https://github.com/AY2627S1-CS2103T-W10-3/tp/issues/129).

### A11. Unknown or mistyped prefixes swallowed into another field
- **What:** `add n/Bob a/1 Road r/note` stores `1 Road r/note` as the address. `edit 1 nn/Bob` saves `nn/Bob` into the name. A repeated prefix gives AB3's "Multiple values specified" message.
- **Why it is egregious:** The data is corrupted silently and the user gets no warning.
- **Examples:** [W08-3#131](https://github.com/AY2627S1-CS2103T-W08-3/tp/issues/131), [W09-3#146](https://github.com/AY2627S1-CS2103T-W09-3/tp/issues/146), [T09-1#262](https://github.com/AY2627S1-CS2103T-T09-1/tp/issues/262), [F13-4#153](https://github.com/AY2627S1-CS2103T-F13-4/tp/issues/153), [CS2103-F12-1#121](https://github.com/AY2627S1-CS2103-F12-1/tp/issues/121).

### A12. Index edge cases and integer overflow
- **What:** `delete 0`, `delete -1` or `delete 99999999999` gives "Invalid command format" instead of "index out of range" (or the reverse). IDs overflow at 2,147,483,647, which in one app **crashed startup**.
- **Examples:** [W11-3#118](https://github.com/AY2627S1-CS2103T-W11-3/tp/issues/118), [CS2103-F10-1#148](https://github.com/AY2627S1-CS2103-F10-1/tp/issues/148), [T15-1 "Client IDs overflow at 2147483647: crashes on startup" (High)](https://github.com/AY2627S1-CS2103T-T15-1/tp/issues/203), [T15-4#164](https://github.com/AY2627S1-CS2103T-T15-4/tp/issues/164).

### A13. Filtered-list state lost after a command (wrong item modified)
- **What:** After `find`, an `edit` resets the list to show everyone, so the indexes shift and the next `delete 1` removes a contact the user never saw. There is also often no indication that a filter is active.
- **Examples:** [T16-2#187 "Index commands can affect another contact after add/edit/tag resets the filter" (Medium)](https://github.com/AY2627S1-CS2103T-T16-2/tp/issues/187), [W13-2#125](https://github.com/AY2627S1-CS2103T-W13-2/tp/issues/125), [W11-2 "edit 1 / delete 1 changes a contact that is not shown" (High)](https://github.com/AY2627S1-CS2103T-W11-2/tp/issues/138), [W11-3#109](https://github.com/AY2627S1-CS2103T-W11-3/tp/issues/109).

---

## 5. Part B: Bugs that are hard to find (need deliberate probing)

These do not show up during normal use. A tester has to **edit the data file, run two copies of the app, use a read-only folder, paste odd Unicode, use a different OS or display scaling, or chain commands that fail**. Fewer testers find them, so each reporter earns more. Several of them **lose data**, which pushes the severity to `Medium` or `High`. AI-assisted testers, who are explicitly allowed, are increasingly finding this class of bug.

### B1. Corrupt or invalid data file silently wiped (the most damaging bug in the corpus)
- **What:** One bad field in `data/*.json` makes the app start with an empty list, and the **next command, even `list` or `help`, overwrites the user's real file**. Variants include a UTF-8 BOM being rejected, duplicate JSON keys, trailing junk after the JSON, and a partially written file after an interrupted save.
- **Rule:** Support for manual editing must be **at least AB3's level**, and the UG must not promise more than the app delivers. The triage guidelines say user mistakes "should not … corrupt the data". Many trackers rated this `High`.
- **Examples:** [W10-4 "Corrupted data file is silently wiped by the next command, even `help`" (High)](https://github.com/AY2627S1-CS2103T-W10-4/tp/issues/143), [F14-1 "A data file that fails to load is overwritten by the first command" (High)](https://github.com/AY2627S1-CS2103T-F14-1/tp/issues/110), [W09-3#148 BOM](https://github.com/AY2627S1-CS2103T-W09-3/tp/issues/148), [T17-3#224 duplicate JSON properties](https://github.com/AY2627S1-CS2103T-T17-3/tp/issues/224), [W08-1 "An interrupted save can replace the last valid data with a partial JSON file" (High)](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/277).
- **Fix:** If loading fails, do **not** save until the user takes an explicit action. Back up the bad file. Write to a temp file and rename it atomically. Tell the user clearly what happened.

### B2. `null` values in the JSON crash startup
- **What:** `"persons": [null]`, `"tags": null` or `"guiSettings": null` in `preferences.json` throws a `NullPointerException` before the window opens.
- **Examples:** [T10-3#140 guiSettings null (High)](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/140), [T10-3#141](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/141), [F11-4#147](https://github.com/AY2627S1-CS2103T-F11-4/tp/issues/147), [T12-3#133](https://github.com/AY2627S1-CS2103T-T12-3/tp/issues/133), [W09-4 "Null policy entry crashes startup" (High)](https://github.com/AY2627S1-CS2103T-W09-4/tp/issues/210).

### B3. Unicode and whitespace edge cases
- **What:** A non-breaking space, an em space, full-width digits or a zero-width character makes two names look identical when they are not, lets a "blank" value through, or causes "Unknown command". Pasted tabs and newlines merge parameters together.
- **Examples:** [W09-2#168](https://github.com/AY2627S1-CS2103T-W09-2/tp/issues/168), [W11-2#169 "Unicode whitespace bypasses duplicate detection"](https://github.com/AY2627S1-CS2103T-W11-2/tp/issues/169), [W08-1#439 "Command word followed by a no-break space gives Unknown command"](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/439), [W09-2#162 pasted tabs and line breaks](https://github.com/AY2627S1-CS2103T-W09-2/tp/issues/162), [T16-1#316 full-width digits](https://github.com/AY2627S1-CS2103T-T16-1/tp/issues/316).

### B4. A second instance overwrites the data
- **What:** Launching the JAR twice (for example, a double-click and a `java -jar`) means whichever copy saves last silently erases the other copy's changes.
- **Examples:** [W12-3#192 (High)](https://github.com/AY2627S1-CS2103T-W12-3/tp/issues/192), [W08-1#284 (High)](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/284), [T17-3#206](https://github.com/AY2627S1-CS2103T-T17-3/tp/issues/206), [T13-4#132](https://github.com/AY2627S1-CS2103T-T13-4/tp/issues/132), [W14-4#168](https://github.com/AY2627S1-CS2103T-W14-4/tp/issues/168).
- **Fix:** Lock the data folder with a lock file, or reload or warn when the file has changed on disk.

### B5. Read-only or unwritable folders
- **What:** The app crashes when launched from a folder it cannot write to, or **read-only commands such as `find` and `list` fail** because the app tries to save after every command.
- **Examples:** [T10-3#156](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/156), [T10-3#143](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/143), [W12-3#177](https://github.com/AY2627S1-CS2103T-W12-3/tp/issues/177), [T14-1#226](https://github.com/AY2627S1-CS2103T-T14-1/tp/issues/226), [W11-4#148](https://github.com/AY2627S1-CS2103T-W11-4/tp/issues/148).

### B6. `StackOverflowError` on very long input (a bug inherited from AB3)
- **What:** AB3's recursive email regex overflows the stack on an email of several thousand characters, such as `a@a.a.a.…`. Similar problems hit long phone numbers and names; in some apps the saved value then **prevents the app from restarting**.
- **Rule:** *Bugs inherited from AB3 are counted.* Because entering this deliberately is close to sabotage, the severity is usually `Low`. It rises when the app can no longer start.
- **Examples:** [T17-1#118](https://github.com/AY2627S1-CS2103T-T17-1/tp/issues/118), [W10-1#148](https://github.com/AY2627S1-CS2103T-W10-1/tp/issues/148), [W08-2#141](https://github.com/AY2627S1-CS2103T-W08-2/tp/issues/141), [T09-2#200 "App fails to start after saving a contact with a very long phone number" (High)](https://github.com/AY2627S1-CS2103T-T09-2/tp/issues/200), [T14-2#182](https://github.com/AY2627S1-CS2103T-T14-2/tp/issues/182).

### B7. A failed save leaves memory and disk out of sync
- **What:** A command fails to save, but the in-memory model has already changed. The UI shows the change, and a retry can delete the next item instead.
- **Examples:** [F10-4 "Failed delete remains applied, causing retry to delete the next contact" (High)](https://github.com/AY2627S1-CS2103T-F10-4/tp/issues/198), [CS2103-F10-2#207](https://github.com/AY2627S1-CS2103-F10-2/tp/issues/207), [T08-1#195](https://github.com/AY2627S1-CS2103T-T08-1/tp/issues/195), [T13-4#117 "A failed edit still changes the student"](https://github.com/AY2627S1-CS2103T-T13-4/tp/issues/117), [W13-4#155](https://github.com/AY2627S1-CS2103T-W13-4/tp/issues/155).

### B8. Cascade and stale references between linked entities
- **What:** After renaming or deleting a parent entity (job, group, student), its children still point to the old one. In one app, deleting by index left a dangling link and **all data was lost on restart**. The detail panel keeps showing an entity that has been deleted.
- **Examples:** [T14-3 "Renaming a job does not update applications" (High)](https://github.com/AY2627S1-CS2103T-T14-3/tp/issues/144), [F14-1#77](https://github.com/AY2627S1-CS2103T-F14-1/tp/issues/77), [W08-2#225](https://github.com/AY2627S1-CS2103T-W08-2/tp/issues/225), [W11-3#98 "Deleting a same-named contact removes another contact's group membership"](https://github.com/AY2627S1-CS2103T-W11-3/tp/issues/98), [F15-1 "Reversing a payment after withdrawing its credit causes a fatal startup error" (High)](https://github.com/AY2627S1-CS2103T-F15-1/tp/issues/252).

### B9. Files written outside the JAR folder (data-sandbox constraint)
- **What:** Data and preferences are resolved relative to the **working directory**, so running `java -jar path/to/app.jar` from another folder writes files there. Imports can also read arbitrary paths.
- **Rule:** *Constraint-Data-Sandbox*. A constraint violation is a `FeatureFlaw`.
- **Examples:** [T10-2 (High)](https://github.com/AY2627S1-CS2103T-T10-2/tp/issues/164), [T10-3 "App files escape JAR home" (High)](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/232), [T17-3 "CSV import allows files outside the application home folder" (High)](https://github.com/AY2627S1-CS2103T-T17-3/tp/issues/135), [F14a-2#98](https://github.com/AY2627S1-CS2103T-F14a-2/tp/issues/98), [T17-1#114](https://github.com/AY2627S1-CS2103T-T17-1/tp/issues/114).

### B10. Not usable at 1280×720 with 150% scaling (screen-resolution constraint)
- **What:** The minimum window height is larger than the scaled screen, so the bottom of the window (often the command box or status bar) is off-screen and cannot be reached.
- **Examples:** [W08-1#251](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/251), [T16-1#330](https://github.com/AY2627S1-CS2103T-T16-1/tp/issues/330), [F09-1#197](https://github.com/AY2627S1-CS2103T-F09-1/tp/issues/197), [T11-1#167](https://github.com/AY2627S1-CS2103T-T11-1/tp/issues/167), [W14-2#193](https://github.com/AY2627S1-CS2103T-W14-2/tp/issues/193).

### B11. Personal data written to logs
- **What:** Full command text (names, phone numbers, emails) is written to `*.log` files and stays there after a contact is deleted.
- **Examples:** [F13-4#127 (Medium)](https://github.com/AY2627S1-CS2103T-F13-4/tp/issues/127), [W13-2#127 (Medium)](https://github.com/AY2627S1-CS2103T-W13-2/tp/issues/127), [W09-2#155 (Medium)](https://github.com/AY2627S1-CS2103T-W09-2/tp/issues/155), [CS2103-F11-2#108](https://github.com/AY2627S1-CS2103-F11-2/tp/issues/108).

### B12. JAR does not run on every platform (single-JAR constraint)
- **What:** The fat JAR bundles only x86-64 JavaFX natives, so it fails on Apple Silicon or ARM Linux. The UG advertises a separate ARM JAR that is missing from the release. Testers on M-series Macs cannot even launch the app.
- **Rule:** *Constraint-Single-File*: one JAR for Linux, macOS (Intel and ARM) and Windows.
- **Examples:** [W08-1#371 (High)](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/371), [W08-4#96 (Medium)](https://github.com/AY2627S1-CS2103T-W08-4/tp/issues/96), [T12-3#129](https://github.com/AY2627S1-CS2103T-T12-3/tp/issues/129).

### B13. Date and time validation
- **What:** `0001-01-01` and `9999-12-31` are accepted with no warning. `2021-02-30` is reported as a "format" error. `t/9:30` is rejected. Past dates are blocked, although the guideline says to warn rather than block.
- **Examples:** [CS2103-F09-1#112](https://github.com/AY2627S1-CS2103-F09-1/tp/issues/112), [W08-1#261](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/261), [W08-1#221](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/221), [T14-4#339](https://github.com/AY2627S1-CS2103T-T14-4/tp/issues/339).

---

## 6. Documentation (UG/DG) mistakes

Testers have a dedicated 30-minute part for documentation (Phase 1 Part II), so documentation bugs are **always looked for**.

| ID | Mistake | Found by | Teams | Examples |
|---|---|---|---|---|
| D1 | **UG/DG contradicts the app**: wrong validation rules, wrong parameter names, behaviour that is not documented | Commonly found | 65+ | [T13-2 "UG gives outdated validation rules for edit" (High)](https://github.com/AY2627S1-CS2103T-T13-2/tp/issues/148), [W08-3#196](https://github.com/AY2627S1-CS2103T-W08-3/tp/issues/196), [T14-2#172](https://github.com/AY2627S1-CS2103T-T14-2/tp/issues/172), [T10-3#161](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/161) |
| D2 | **Template placeholders left in**: `{ more test cases … }`, "More to be added", Acknowledgements still a placeholder, DG architecture still describing AB3 | Commonly found | 28+ | [T13-2 "DG Acknowledgements is still a placeholder" (High)](https://github.com/AY2627S1-CS2103T-T13-2/tp/issues/146), [T17-3#212](https://github.com/AY2627S1-CS2103T-T17-3/tp/issues/212), [T17-3#214](https://github.com/AY2627S1-CS2103T-T17-3/tp/issues/214), [W08-1#295](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/295) |
| D3 | **Outdated screenshots**, including help screenshots that still show the AB3 URL | Commonly found | 26+ | [T10-3#133](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/133), [W08-1#226](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/226), [W08-1#230](https://github.com/AY2627S1-CS2103T-W08-1/tp/issues/230) |
| D4 | **UML errors or omissions**: sequence diagrams not updated for new behaviour, class diagrams missing new classes, wrong return types, multiplicities missing | Moderately hard (the tester must compare the diagram with the code or behaviour) | 36+ | [F10-1#116](https://github.com/AY2627S1-CS2103T-F10-1/tp/issues/116), [T10-3#175](https://github.com/AY2627S1-CS2103T-T10-3/tp/issues/175), [W09-4#205](https://github.com/AY2627S1-CS2103T-W09-4/tp/issues/205), [T17-3#218](https://github.com/AY2627S1-CS2103T-T17-3/tp/issues/218) |
| D5 | **User stories, use cases, NFRs or glossary**: stories without an implemented/not-implemented status, use cases missing extensions, NFRs that cannot be verified, important terms missing | Moderately hard | 31+ | [T17-3#215](https://github.com/AY2627S1-CS2103T-T17-3/tp/issues/215), [CS2103-F11-1#158](https://github.com/AY2627S1-CS2103-F11-1/tp/issues/158), [W08-3#188](https://github.com/AY2627S1-CS2103T-W08-3/tp/issues/188) |
| D6 | **Broken links** in the UG table of contents or command summary | Commonly found | n/a | [F10-4 "UG links to findc, findhc, hidec… are broken" (High)](https://github.com/AY2627S1-CS2103T-F10-4/tp/issues/172), [T16-1#498](https://github.com/AY2627S1-CS2103T-T16-1/tp/issues/498) |

Severity reminders from the rules: typos are `VeryLow` and **never `NotInScope`**. Broken links are `Low` or `Medium`. UML notation errors are `Low` or higher. An NFR the app does not meet is a `Low` documentation bug, or a `FeatureFlaw` if the NFR was reasonable in the first place.

---

## 7. Procedural mistakes during the PE (these lose marks even without a bug)

Every item below comes directly from the AY26/27 S1 rules.

### As a developer (Phase 2)
1. **No comment starting with `# T`.** The team is treated as having given no response, and disputes go to the tester.
2. **Editing the issue title or body.** The response becomes invalid and the bug is accepted as the tester reported it.
3. **More than 50% of bugs unassigned**, where assigning to everyone also counts as unassigned. Every member's penalty rises by 20%.
4. **Overusing `NotApplicable`** when `NotInScope` fits. This lowers your accuracy.
5. **Duplicates nested more than one level deep**, or **writing `#23` in a justification**. Cross-references stop working after the comments are transferred. Name the duplicate by its title instead.
6. **Marking a higher-severity bug as a duplicate of a lower-severity one without justification.** This counts as an unjustified downgrade.
7. **Arguing for an extreme position** (for example, VeryLow when the tester chose High). Tutors pick one side and never a middle ground.
8. **Using `CannotReproduce` without a real effort** to work out the cause from screenshots.
9. **Changing labels provided by the course**, adding labels prefixed with `type.` or `severity.`, transferring issues, or creating new issues.
10. **Writing more than 500 words**, or putting several objections in one comment.

### As a tester (Phase 1 and Phase 3)
1. **Putting several bugs in one report.** The developer chooses only one of them.
2. **Adding details in comments** instead of the description. Comments are ignored.
3. **Not justifying the severity** in the description of each selected bug.
4. **Choosing too high a severity.** The rule is "when in doubt, go lower".
5. **Choosing bugs by type.** Choose by severity and confidence; all types are treated as equal.
6. **Objecting to every downgrade.** Objections that fail lower your accuracy.
7. **Not stating "should be `NotInScope`"** when that is what you are arguing. Without it, the system assumes you want `Accepted`.
8. **Skipping the TEAMMATES overall evaluation.** This brings an automatic penalty.

---

## 8. Pre-PE checklist

Run through this list on the **released v1.6 JAR**, from an **empty folder**, on **Java 25**.

**Five-minute smoke test (commonly found bugs)**
- [ ] The help window, window title, messages, data file name, JAR name and screenshots contain **no AB3 or "AddressBook" leftovers**.
- [ ] `clear abc`, `exit 1`, `list x` and `help me` are either rejected or behave safely; `clear` asks for confirmation or can be undone.
- [ ] `add` accepts names like `Rahul s/o Kumar`, `Mary-Jane O'Neil`, `Zoë Tan` and `Lim, Jr.`
- [ ] Phone `+65 9123 4567` and `9123-4567` are accepted, or rejected with a clear reason. `+++1+++` and a 30-digit number are rejected.
- [ ] `john doe`, `John  Doe` and `JOHN DOE` are flagged as duplicates (or the UG states this limitation clearly).
- [ ] Tags and command words are case-insensitive, or the UG says otherwise.
- [ ] `delete 0`, `delete -1`, `delete 999999999999` and `delete abc` give specific, correct errors.
- [ ] `add n/X a/Road r/typo` does not silently store `r/typo` inside the address.
- [ ] `find` then `edit 1 …` then `delete 1` affects the expected entry, and the UI shows that a filter is active.
- [ ] Long names, tags and addresses wrap or are readable; the result box wraps.

**Deep probes (bugs that are hard to find)**
- [ ] Corrupt one field in the data file and start the app. The original file is **preserved or backed up** and not overwritten by the first command.
- [ ] Insert `null`, a UTF-8 BOM, duplicate keys and trailing junk into the data file and `preferences.json`. There is no crash.
- [ ] Run two instances at once. There is no silent loss of data.
- [ ] Run from a read-only folder. There is no crash, and read-only commands still work.
- [ ] Run `java -jar sub/app.jar` from another directory. All files are created next to the JAR.
- [ ] Paste a 5,000-character email or name. There is no `StackOverflowError`, and the app still restarts afterwards.
- [ ] Paste NBSP, em-space, full-width digits, tabs and newlines into every field and command word.
- [ ] Make a save fail (for example, a read-only data file) during add, edit and delete. The in-memory state is rolled back.
- [ ] Delete or rename parent entities. No dangling or stale references remain, and the app restarts cleanly.
- [ ] At 1280×720 with 150% scaling, every part of the UI can be reached.
- [ ] Log files contain no personal data.
- [ ] The JAR launches on an Apple Silicon Mac and on Linux.
- [ ] `Feb 30`, `0001-01-01` and `t/9:30` are handled sensibly.

**Documentation**
- [ ] Every command in the UG matches the app: the same parameter names, rules and error behaviour.
- [ ] There are no placeholders (`{ more … }`, "to be added", template Acknowledgements).
- [ ] Every link in the table of contents and the command summary works.
- [ ] Sequence and class diagrams reflect v1.6, including any new classes and confirmation steps.
- [ ] User stories show whether each is implemented; use cases have extensions; NFRs can be verified.
- [ ] Known limitations are listed in the UG under **Known issues** or a **Planned enhancements** appendix. This lowers severity and supports a `NotInScope` response.

**Feature-freeze reminder:** fixes made after the v1.5 deadline count towards the limit of **15% or 150 LoC per member** in `src/main` (up to 20% with tolerance). Fix the High and Medium bugs first and list the rest under *Planned Enhancements*.

---

*Generated by analysing the AY26/27 S1 CS2103T course website source and 4,316 alpha- and beta-test bug reports from 114 AY26/27 S1 team repositories. Counts come from keyword matching on issue titles and are lower bounds. Issue links point to public team repositories.*
