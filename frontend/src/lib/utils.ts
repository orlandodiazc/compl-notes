import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import { API_BASE_URL } from "./api";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getUserImgSrc(imageId?: string) {
  if (!imageId) return undefined;
  return `${API_BASE_URL}/user-images/${imageId}`;
}

export function getNoteImgSrc(imageId?: string) {
  if (!imageId) return undefined;
  return `${API_BASE_URL}/note-images/${imageId}`;
}

export function getNameInitials(name?: string) {
  if (!name) return;
  return name
    .split(" ")
    .map((n) => n.charAt(0))
    .join("")
    .toUpperCase();
}
