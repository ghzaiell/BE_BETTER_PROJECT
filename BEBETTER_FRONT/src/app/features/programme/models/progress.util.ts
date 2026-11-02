import { Subject } from './subject.model';

export interface Progress {
  done: number;
  total: number;
  percent: number; // 0..100, rounded
}

export function subjectProgress(subject: Subject): Progress {
  const missions = subject.missions ?? [];
  const done = missions.filter(m => m.etat).length;
  return toProgress(done, missions.length);
}

export function overallProgress(subjects: Subject[]): Progress {
  let done = 0;
  let total = 0;
  for (const s of subjects) {
    const p = subjectProgress(s);
    done += p.done;
    total += p.total;
  }
  return toProgress(done, total);
}

function toProgress(done: number, total: number): Progress {
  return { done, total, percent: total === 0 ? 0 : Math.round((done / total) * 100) };
}
