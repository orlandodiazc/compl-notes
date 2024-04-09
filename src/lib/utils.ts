import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import { API_BASEURL } from "./api";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getUserImgSrc(imageId?: string) {
  return `${API_BASEURL}/user-images/${imageId}`;
}

export function getNoteImgSrc(imageId: string) {
  return `${API_BASEURL}/note-images/${imageId}`;
}

export function getNameInitials(name?: string) {
  if (!name) return;
  return name
    .split(" ")
    .map((n) => n.charAt(0))
    .join();
}
