import os
import glob

extensions = {
        1: "jpg",
        2: "png",
        3: "jpeg"
    }
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

def get_slash():
    """
    Method to get slash based on operating system where is script running
    """
    if os.name == 'nt':
        return "\\"
    else:
        return "/"

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def save_image(file,id,upload_folder):
    """
    Method that will save image to upload destination under given id
    """
    try:
        if file.filename == '':
            return None
        if file and allowed_file(file.filename):
            filename = file.filename
            extension = filename.rsplit('.', 1)[1].lower()
            file.save(os.path.join(upload_folder, id+"."+extension))
            for ext in extensions:
                if extension == extensions[ext]:
                    return ext
            return None
    except Exception as e:
        print(e)
        return None
    
def get_image_file(upload_folder,id,image_type):
    """
    Methot that will get image from disk with given id
    """
    slash = get_slash()
    filename = f"{upload_folder}{slash}{id}.{extensions[image_type]}"
    return (filename,'image/'+ extensions[image_type])

def get_profile_image_file(upload_folder,id):
    """
    Methot that will get image from disk with given id
    """
    slash = get_slash()
    image = glob.glob(f'{upload_folder}{slash}{id}.*')
    return (image[0],'image/'+ image[0].rsplit('.', 1)[1].lower())



