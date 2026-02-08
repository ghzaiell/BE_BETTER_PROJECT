export interface Mission {
  id?: string;         // optional because backend generates it
  name: string;
  etat: boolean;
  description: string;
}