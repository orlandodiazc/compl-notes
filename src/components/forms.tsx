import {
  FieldMetadata,
  unstable_useControl as useControl,
} from "@conform-to/react";
import React, { ElementRef, useRef } from "react";
import { Checkbox } from "./ui/checkbox";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { Textarea } from "./ui/textarea";

export type ListOfErrors = Array<string | null | undefined> | null | undefined;

export function ErrorList({
  id,
  errors,
}: {
  errors?: ListOfErrors;
  id?: string;
}) {
  const errorsToRender = errors?.filter(Boolean);
  if (!errorsToRender?.length) return null;
  return (
    <ul id={id} className="flex flex-col gap-1">
      {errorsToRender.map((e) => (
        <li key={e} className="text-foreground-destructive text-body-2xs">
          {e}
        </li>
      ))}
    </ul>
  );
}

export function Field({
  labelProps,
  inputProps,
  errors,
  className,
}: {
  labelProps: React.LabelHTMLAttributes<HTMLLabelElement>;
  inputProps: React.InputHTMLAttributes<HTMLInputElement>;
  errors?: ListOfErrors;
  className?: string;
}) {
  const errorId = inputProps["aria-describedby"];
  return (
    <div className={className}>
      <Label htmlFor={inputProps.id} {...labelProps} />
      <Input {...inputProps} className="aria-invalid:border-input-invalid" />
      <div className="min-h-[32px] px-2 pb-3 pt-2">
        {errorId ? <ErrorList id={errorId} errors={errors} /> : null}
      </div>
    </div>
  );
}

export function TextareaField({
  labelProps,
  textareaProps,
  errors,
  className,
}: {
  labelProps: React.LabelHTMLAttributes<HTMLLabelElement>;
  textareaProps: React.TextareaHTMLAttributes<HTMLTextAreaElement>;
  errors?: ListOfErrors;
  className?: string;
}) {
  const errorId = textareaProps["aria-describedby"];

  return (
    <div className={className}>
      <Label htmlFor={textareaProps.id} {...labelProps} />
      <Textarea
        {...textareaProps}
        className="aria-invalid:border-input-invalid"
      />
      <div className="min-h-[32px] px-4 pb-3 pt-1">
        {errorId ? <ErrorList id={errorId} errors={errors} /> : null}
      </div>
    </div>
  );
}

export function CheckboxConformField({
  labelProps,
  meta,
  errors,
  className,
}: {
  labelProps: JSX.IntrinsicElements["label"];
  meta: Parameters<typeof CheckboxConform>[0]["meta"];
  errors?: ListOfErrors;
  className?: string;
}) {
  const errorId = meta.errorId;
  meta;
  return (
    <div className={className}>
      <div className="flex gap-2">
        <CheckboxConform meta={meta} />
        <label
          htmlFor={meta.id}
          {...labelProps}
          className="self-center text-body-xs text-muted-foreground"
        />
      </div>
      <div className="px-4 pb-3 pt-1">
        {errorId ? <ErrorList id={errorId} errors={errors} /> : null}
      </div>
    </div>
  );
}

function CheckboxConform({
  meta,
}: {
  meta: FieldMetadata<string | boolean | undefined>;
}) {
  const checkboxRef = useRef<ElementRef<typeof Checkbox>>(null);
  const control = useControl(meta);

  return (
    <>
      <input
        className="sr-only"
        aria-hidden
        ref={control.register}
        name={meta.name}
        tabIndex={-1}
        defaultValue={meta.initialValue}
        onFocus={() => checkboxRef.current?.focus()}
      />
      <Checkbox
        ref={checkboxRef}
        id={meta.id}
        checked={control.value === "on"}
        onCheckedChange={(checked) => {
          control.change(checked ? "on" : "");
        }}
        onBlur={control.blur}
        className="focus:ring-stone-950 focus:ring-2 focus:ring-offset-2"
      />
    </>
  );
}
