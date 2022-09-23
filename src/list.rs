use std::path::PathBuf;
use std::time::UNIX_EPOCH;
use serde::{Serialize};

#[derive(Serialize)]
enum FileType {
    DIRECTORY,
    FILE,
}

#[derive(Serialize)]
pub struct FileEntry {
    size: Option<u64>,
    time: u64,
    name: String,
    file_type: FileType,
}

pub fn list(path: &PathBuf) -> Vec<FileEntry> {
    if let Ok(entries) = path.read_dir() {
        entries
            .filter_map(Result::ok)
            .map(|e| {
                let metadata = e.metadata().unwrap();
                let size = metadata.len();
                let time = metadata.modified().unwrap().duration_since(UNIX_EPOCH).unwrap().as_secs();
                let name = e.file_name().to_string_lossy().into_owned();
                let file_type = e.file_type().unwrap();

                if file_type.is_dir() {
                    FileEntry {
                        size: None,
                        time,
                        name,
                        file_type: FileType::DIRECTORY,
                    }
                } else {
                    FileEntry {
                        size: Some(size),
                        time,
                        name,
                        file_type: FileType::FILE,
                    }
                }
            })
            .collect::<Vec<FileEntry>>()
    } else {
        vec![]
    }
}