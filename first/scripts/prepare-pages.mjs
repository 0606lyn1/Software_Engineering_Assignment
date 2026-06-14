import { copyFile } from 'node:fs/promises'
import { resolve } from 'node:path'

const distDir = resolve(import.meta.dirname, '..', 'dist')

await copyFile(resolve(distDir, 'index.html'), resolve(distDir, '404.html'))
