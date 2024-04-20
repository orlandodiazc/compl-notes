import { components } from "./schema";

export type ApiSchema = components["schemas"];

export interface ApiProblemDetail {
  type: string;
  title: string;
  status?: number;
  detail?: string;
  instance?: string;
  errors?: Record<string, string>;
}
