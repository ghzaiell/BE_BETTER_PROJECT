import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subject } from '../models/subject.model';
import { Mission } from '../models/mission.model';

@Injectable({
  providedIn: 'root'
})
export class ProgrammeServiceService {

  private apiUrl = 'http://localhost:8080/subject'; // change if needed

  constructor(private http: HttpClient) { }

  // ----------------------------------------
  // CREATE A NEW SUBJECT
  // ----------------------------------------
  addSubject(username: string, prompt: string): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/newSubjects/${username}`,
      prompt,
      { responseType: 'json' }
    );
  }

  // ----------------------------------------
  // DELETE SUBJECT
  // ----------------------------------------
  deleteSubject(username: string, subjectId: string): Observable<string> {
    return this.http.delete(`${this.apiUrl}/deleteSubject/${username}/${subjectId}`, {
      responseType: 'text'
    });
  }

  // ----------------------------------------
  // UPDATE MISSION DONE / UNDONE
  // ----------------------------------------
  updateMissionStatus(username: string, subjectId: string, missionId: string): Observable<string> {
    return this.http.put(`${this.apiUrl}/done/${username}/${subjectId}/${missionId}`, {}, {
      responseType: 'text'
    });
  }

  // ----------------------------------------
  // DELETE ONE MISSION
  // ----------------------------------------
  deleteMission(username: string, subjectId: string, missionId: string): Observable<string> {
    return this.http.delete(
      `${this.apiUrl}/deleteMission/${username}/${subjectId}/${missionId}`,
      { responseType: 'text' }
    );
  }

  // ----------------------------------------
  // GET ONE SUBJECT
  // ----------------------------------------
  getSubject(username: string, subjectId: string): Observable<Subject> {
    return this.http.get<Subject>(
      `${this.apiUrl}/getSubject/${username}/${subjectId}`
    );
  }

  // ----------------------------------------
  // GET ALL SUBJECTS FOR USER
  // ----------------------------------------
  getAllSubjects(username: string): Observable<Subject[]> {
    return this.http.get<Subject[]>(
      `${this.apiUrl}/getAllSubjects/${username}`
    );
  }

  // ----------------------------------------
  // GET MISSIONS OF ONE SUBJECT
  // ----------------------------------------
  getMissions(username: string, subjectId: string): Observable<Mission[]> {
    return this.http.get<Mission[]>(
      `${this.apiUrl}/getMissions/${username}/${subjectId}`
    );
  }
}
